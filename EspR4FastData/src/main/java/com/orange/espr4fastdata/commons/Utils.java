package com.orange.espr4fastdata.commons;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.Duration;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.orange.espr4fastdata.cep.EventSinkUrl;
import com.orange.espr4fastdata.cep.EventSinkUrlList;
import com.orange.espr4fastdata.cep.Statement;

public class Utils {
  private static Logger logger = Logger.getLogger(Utils.class);

//----------------------------------------------------------------------------------------------------------------------
/**
 * Call an URI with the chosen HTTP method. Carry the specified payload provided as a String object.
 * @param targetUrl the URI to call.
 * @param httpMethod the HTTP method to use. example: POST, GET etc...
 * @param requestPayload the payload content. 
 * @param xAuthToken TODO
 * @return the HTTP return code following the call
 * @throws IOException
 * @throws IllegalStateException
 * @throws IllegalArgumentException
 * @throws ProtocolException
 */
  public static int callURL(String targetUrl, 
  													String contentType,
  													String httpMethod, 
  													String requestPayload,
  													StringBuilder responsePayload, 
  													String xAuthToken, 
  													String proxyUrl, Integer proxyPort ) 
    
  	throws IOException, IllegalStateException, ProtocolException {
     
    try {
      URL url = new URL(targetUrl);
      HttpURLConnection httpUrlConnection;
 
      if(proxyUrl == null) {
       	httpUrlConnection = (HttpURLConnection)url.openConnection();
      }
      else {
      	Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl, proxyPort));
      	httpUrlConnection = (HttpURLConnection)url.openConnection(proxy);
      }
      logger.info("About to call the following URL: ("+httpMethod+") "+targetUrl);
      logger.info("With payload:\n"+requestPayload );
      
      httpUrlConnection.setDoOutput(true);
      httpUrlConnection.setRequestMethod(httpMethod);
      httpUrlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
      
      if(targetUrl.equals("http://localhost:18080/ngsi10/notifyContext"))
      	System.out.println();
      
      httpUrlConnection.setRequestProperty("Content-Type", contentType);
      
      if(xAuthToken != null) 
      	httpUrlConnection.setRequestProperty("X-Auth-Token", xAuthToken);
      
      httpUrlConnection.setFixedLengthStreamingMode(requestPayload.getBytes().length);
      
      httpUrlConnection.getOutputStream().write(requestPayload.getBytes());
      
      InputStream inputStream = httpUrlConnection.getInputStream();
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      
      int httpCode = httpUrlConnection.getResponseCode();

      String readLine;

      while((readLine = bufferedReader.readLine()) != null) {
      	responsePayload.append(readLine+'\n');
      }

      httpUrlConnection.getContent().toString();
      
      return httpCode;
    }
    catch(ProtocolException e) {throw new IllegalArgumentException(e); }
    catch(IllegalStateException e) { throw new IllegalStateException(e); }
    catch(IOException e) { throw new IOException(e); }
  }
//----------------------------------------------------------------------------------------------------------------------
/**
 * Serialize any Object to XML string representation. 
 * @param o the object to serialize to an XML String representation.
 * @return the resulting XML string.
 */
  public static String objectToFormattedXmlString(Object o) {
  	
  StringWriter sw = new StringWriter();
  
  try{
    JAXBContext context = JAXBContext.newInstance(o.getClass());
    Marshaller m = context.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    m.marshal(o, sw);
  }
  catch(JAXBException e){ logger.error(e.getMessage()); }

  return sw.toString();
  }
//----------------------------------------------------------------------------------------------------------------------
/**
 * Serialize any Object to XML string representation. 
 * @param o the object to serialize to an XML String representation.
 * @return the resulting XML string.
 */
  public static String objectToFormattedJsonString( Object object, String dateFormat ) {
  	
  	String result = null;
		ObjectMapper objectMapper = new ObjectMapper();
		
		try {
			objectMapper.setDateFormat(new SimpleDateFormat(dateFormat));
			objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			result = objectMapper.writeValueAsString(object);
		} 
		catch (JsonProcessingException e) {	logger.error(e.getMessage()); } 
		catch (IOException e) {	logger.error(e.getMessage()); } 
  	
		return result;
  }
//----------------------------------------------------------------------------------------------------------------------
/**
 * Serialize any Object to XML string representation. 
 * @param o the object to serialize to an XML String representation.
 * @return the resulting XML string.
 */
  public static String objectToFormattedJsonString( Object object ) {
  	
  	String result = null;
		ObjectMapper objectMapper = new ObjectMapper();
		
		// TODO Remove the following 4 lines
		Statement statement = new Statement();
		EventSinkUrlList eventSinkUrlList = new EventSinkUrlList();
		statement.setEventSinkUrlList(eventSinkUrlList);
		statement.getEventSinkUrlList().getList().add(new EventSinkUrl());
		
		//object = new EventSinkUrl();
		
		try {
			objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
			result = objectMapper.writeValueAsString(object);
		} 
		catch (JsonProcessingException e) {	logger.error(e.getMessage()); } 
		catch (IOException e) {	logger.error(e.getMessage()); } 
  	
		return result;
  }
//----------------------------------------------------------------------------------------------------------------------  
/** 
 * Deserialize an XML String to an object.
 * @param xmlString the XML String to convert.
 * @return a java object.
 */
  public static Object xmlStringToObject(String xmlString, Class<?> cls) throws JAXBException,
  																																							InstantiationException,
  																																							IllegalAccessException {
    Object object = null;
 		object = cls.newInstance();
  		
 	  JAXBContext jaxbContext = JAXBContext.newInstance(cls);
 	  Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

 	  StringReader reader = new StringReader(xmlString);
 	  object = unmarshaller.unmarshal(reader);
  	
  	return object;
  }  
//----------------------------------------------------------------------------------------------------------------------
  public static Object jsonStringToObject(String json, Class<?> cls) {
		
    ObjectMapper mapper = new ObjectMapper(); 
    Object object = null;
    try {
			object = mapper.readValue(json, cls);
		} 
    catch (JsonParseException e) {logger.error(e.getMessage());	} 
    catch (JsonMappingException e) {logger.error(e.getMessage());	} 
	  catch (IOException e){logger.error(e.getMessage());	} 
  	
  	return object;  	
  }
//----------------------------------------------------------------------------------------------------------------------  
/**
 * Return the current date in milliseconds.
 * @return the current date in milliseconds as a String object.
 */
  public static long getCurrentGmtTimeStampInMs() {
    return Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis();
  }
  //--------------------------------------------------------------------------------------------------------------------  
  /**
   * Store the text content of a file in a String.
   * @param filePath the file name
   * @return a String that features the file content.
   * @throws IOException
   */
  public static String readTextContentFromFile( String filePath ) throws IOException {
    BufferedReader bufferedReader = new BufferedReader( new FileReader (filePath));
    StringBuilder stringBuilder = new StringBuilder();
    String ls = System.getProperty("line.separator");

    String line = null;
    try {
    	while( ( line = bufferedReader.readLine() ) != null ) {
    		stringBuilder.append( line );
    		stringBuilder.append( ls );
      }
    }
    catch(IOException e) { throw new IOException(e); }
    
    bufferedReader.close();

    return stringBuilder.toString();
  }
//----------------------------------------------------------------------------------------------------------------------  
  /**
   * Overwrite text content to a file. If the file does not exist it is created. If it already exists it is overwritten.
   * @param filePath the file path.
   * @param textContent the text content to write within the file.
   */
  public static void writeTextContentToFile(String filePath, String textContent) 
  		throws FileNotFoundException,  
  		       IOException {	
  	try {
  		if( filePath.contains("Statement")) {
  			System.out.println("");
  		}
			Writer writer = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(filePath), "utf-8") );
			writer.write(textContent);
			writer.close();
		} 
		catch (UnsupportedEncodingException e) { logger.error(e.getMessage());	} 
		catch (FileNotFoundException e) {	throw new FileNotFoundException(); } 
		catch (IOException e) {	throw new IOException(e); }
  }
//----------------------------------------------------------------------------------------------------------------------  
  /**
   * Check if a file exists.
   * @param filePath the path to the file.
   * @return true if the file exists, false otherwise.
   */
  public static boolean fileExists(String filePath) {
  	File f = new File(filePath);
  	if(f.exists()) { return true; }

  	return false;
  }
//----------------------------------------------------------------------------------------------------------------------  
  /**
   * @param filePath
   * @return true if the file was deleted, false if an error has occured.
   */
  public static boolean deleteFile(String filePath) { 

  	File file = new File(filePath);
		if(file.delete()) { return true; }

		return false;
  }
//----------------------------------------------------------------------------------------------------------------------
  public static String javaDurationToIso8601Duration(Duration duration) {
  	String isoDuration = "P";
  	
  	if(duration.getYears() != 0) isoDuration += duration.getYears()+"Y";
  	if(duration.getMonths() != 0) isoDuration += duration.getMonths()+"M";
  	if(duration.getDays() != 0) isoDuration += duration.getDays()+"D";
  	
  	if(duration.getHours() != 0 || duration.getMinutes() != 0 || duration.getSeconds() != 0 ) isoDuration += "T";
  	
  	if(duration.getHours() != 0) isoDuration += duration.getHours()+"H";
  	if(duration.getMinutes() != 0) isoDuration += duration.getMinutes()+"M";
  	if(duration.getSeconds() != 0) isoDuration += duration.getSeconds()+"S";  	
  	
  	return isoDuration;
  }
//----------------------------------------------------------------------------------------------------------------------  
  public static String msTimeStampToCustomDateFormat(long timeInMs) {
    
  	Calendar calendar = Calendar.getInstance();
  	calendar.setTimeInMillis(timeInMs);
  	
  	Date date = calendar.getTime();
    
  	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
    
  	String iso8601FormattedDate = simpleDateFormat.format(date).substring(0, 22) + ":" 
    															+ simpleDateFormat.format(date).substring(22);
  	
    return iso8601FormattedDate;
}
//----------------------------------------------------------------------------------------------------------------------
  
  public static String getFormattedTime( String format ) {
  	Calendar calendar = Calendar.getInstance();
  	
  	Date date = calendar.getTime();
  	
  	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
  	
  	String result = simpleDateFormat.format(date);
  	
  	return result;	
  }
//----------------------------------------------------------------------------------------------------------------------
/** Transform ISO 8601 string to Calendar. */
  public static long iso8601ToMsTimeStamp(String iso8601String) throws ParseException {
  
  	Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

    try {
    	iso8601String = iso8601String.substring(0, 22) + iso8601String.substring(23);  // to get rid of the ":"
    } 
    catch (IndexOutOfBoundsException e) {
      throw new ParseException("Invalid length", 0);
    }
    Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz").parse(iso8601String);
    calendar.setTime(date);
    return calendar.getTimeInMillis();
  }	
//----------------------------------------------------------------------------------------------------------------------
  public static String getEnvVar(String name) {
  	Map<String, String> env = System.getenv();
  	
  	for (String envName : env.keySet()) {      
  		if(name.equals(envName)) return env.get(envName);
    }
		return null;
  }  
//----------------------------------------------------------------------------------------------------------------------
  public static List<String> getDirectoryContent(String path) {
  	File file = new File(path);
  	String[] directoryContent = file.list();
  	
  	return Arrays.asList(directoryContent);
  }
//----------------------------------------------------------------------------------------------------------------------
  
  public static String getSmallUUID() {
  	return UUID.randomUUID().toString().split("-")[0];
  }  
//----------------------------------------------------------------------------------------------------------------------  
/**
 * Return the current date in milliseconds.
 * @return the current date in milliseconds as a String object.
 */
  public static String getIso8601GmtTimeStamp() {
    return String.format("%04d-", Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.YEAR))+
    			 String.format("%02d-", Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.MONTH))+
    			 String.format("%02dT", Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.DAY_OF_MONTH))+
    			 String.format("%02d:", Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.HOUR))+
    			 String.format("%02d:", Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.MINUTE))+
    			 String.format("%02d+00:00", Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.SECOND));    
  }
//----------------------------------------------------------------------------------------------------------------------
  public static Date getDateFromString( String dateAsString, String format )
     	throws ParseException {
     	
      return new SimpleDateFormat(format).parse(dateAsString);
    }
//----------------------------------------------------------------------------------------------------------------------
  
  public static String getType(Field field) {
  	
    ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
    Class<?> cls = (Class<?>) parameterizedType.getActualTypeArguments()[0];
    return cls.getSimpleName();
  }
//----------------------------------------------------------------------------------------------------------------------
  public static String formatDate(Date date, String format) {
  	
  	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
  	
  	String result = simpleDateFormat.format(date);
  	
  	return result;
  }
}