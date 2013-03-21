package fr.certu.chouette.exchange.netex.importer;

import com.ximpleware.EOFException;
import com.ximpleware.EncodingException;
import com.ximpleware.EntityException;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.exchange.netex.importer.converters.LineConverter;
import fr.certu.chouette.exchange.netex.importer.converters.NeptuneConverter;
import fr.certu.chouette.model.neptune.Line;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class NetexFileReader {
    
    private static final Logger logger = Logger.getLogger(NetexFileReader.class);   
   
    private VTDGen vg = new VTDGen(); 
    
    /**
     * extract Neptune object from file
     * 
     * @param fileName file relative or absolute path 
     * @return Neptune model
     */
    public Line  readInputStream(InputStream inputStream) throws IOException, EncodingException, EOFException, EntityException, ParseException, XPathParseException, XPathEvalException, NavException, java.text.ParseException 
    {
        byte[] b = IOUtils.toByteArray(inputStream);       
        vg.setDoc(b);
        vg.parse(true); // set namespace awareness to true
       
        VTDNav nav = vg.getNav();
        
        NeptuneConverter neptuneConverter = new NeptuneConverter(nav);
        
        Line line = neptuneConverter.convert();                
                
        return line;
        
    }

//    /**
//     * extract Neptune object from file
//     * 
//     * @param fileName  file relative or absolute path 
//     * @param validation validate XMl
//     * @return Neptune model
//     */
//    public ChouettePTNetworkTypeType read(String fileName, boolean validation) {
//        String content = null;
//        try {
//            logger.debug("READ " + fileName);
//            content = readStream(fileName, new FileInputStream(fileName));
//        } catch (Exception e) {
//            String msg = e.getClass().getName() + ":" + e.getMessage();
//            LoggingManager.log(logger, msg, Level.ERROR);
//            throw new ExchangeRuntimeException(ExchangeExceptionCode.FILE_NOT_FOUND, e, fileName);
//        }
//        ChouettePTNetworkTypeType chouettePTNetworkType = parseXML(fileName, content, validation, false);
//        return chouettePTNetworkType;
//    }
//
//    /**
//     * extract Neptune object from inputStream (for ZipFile usage)
//     * 
//     * @param zip zipFile 
//     * @param entry entry to extract
//     * @param validation
//     * @return Neptune model
//     */
//    public ChouettePTNetworkTypeType read(ZipFile zip, ZipEntry entry) {
//        String content = null;
//        String inputName = entry.getName();
//        InputStream input = null;
//        try {
//            logger.debug("READ zipped file " + inputName);
//            input = zip.getInputStream(entry);
//            content = readStream(inputName, input);
//        } catch (Exception e) {
//            String msg = e.getClass().getName() + ":" + e.getMessage();
//            LoggingManager.log(logger, msg, Level.ERROR);
//            throw new ExchangeRuntimeException(ExchangeExceptionCode.FILE_NOT_FOUND, e, inputName);
//        } finally {
//            if (input != null) {
//                try {
//                    input.close();
//                } catch (IOException e) {
//                    LoggingManager.log(logger, "fail to close entry", Level.WARN, e);
//                }
//            }
//        }
//
//        ChouettePTNetworkTypeType chouettePTNetworkType = parseXML(inputName, content, validation, true);
//        return chouettePTNetworkType;
//    }
//
//    /**
//     * convert string data to Neptune model
//     * 
//     * @param contentName source name for logging purpose
//     * @param content string content to parse
//     * @return Neptune model
//     */
//    private ChouettePTNetworkTypeType parseXML(String contentName, String content, boolean validation, boolean isZipEntry) {
//        ChouettePTNetworkTypeType chouettePTNetworkType = null;
//
//        try {
//            logger.debug("UNMARSHALING content of " + contentName);
//            Unmarshaller anUnmarshaller = new Unmarshaller(ChouettePTNetwork.class);
//            anUnmarshaller.setIgnoreExtraElements(false);
//            anUnmarshaller.setValidation(validation);
//            chouettePTNetworkType = (ChouettePTNetworkTypeType) anUnmarshaller.unmarshal(new StringReader(content));
//            logger.debug("END OF UNMARSHALING content of " + contentName);
//        } catch (org.exolab.castor.xml.ValidationException ex) {
//            org.exolab.castor.xml.ValidationException e = ex;
//            logger.debug("ValidationException " + e.getMessage());
//            do {
//                String msg = e.getMessage();
//                LoggingManager.log(logger, msg, Level.ERROR);
//                e = e.getNext();
//            } while (e != null);
//            throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_NEPTUNE_FILE, ex, contentName);
//        } catch (MarshalException e) {
//            if ((e instanceof MarshalException) && (e.getCause() != null) && (e.getCause() instanceof SAXException)) {
//                File file = null;
//                try {
//                    if (isZipEntry) {
//                        file = new File(contentName);
//                        java.io.FileWriter fw = new java.io.FileWriter(file);
//                        fw.write(content);
//                        fw.flush();
//                        fw.close();
//                    }
//                    test_xml(contentName);
//                    if (file != null) {
//                        file.delete();
//                    }
//                } catch (SAXParseException e1) {
//                    if (file != null) {
//                        file.delete();
//                    }
//                    String msg1 = e1.getMessage() + " AT LINE " + e1.getLineNumber() + " COLUMN " + e1.getColumnNumber();
//                    logger.error("SAXParseException " + msg1);
//                    LoggingManager.log(logger, msg1, Level.ERROR);
//                    throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_XML_FILE, e1, contentName);
//                } catch (Exception e1) {
//                    if (file != null) {
//                        file.delete();
//                    }
//                    String msg1 = e1.getMessage();
//                    logger.error("Exception " + msg1);
//                    LoggingManager.log(logger, msg1, Level.ERROR);
//                    throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_XML_FILE, e1, contentName);
//                }
//            }
//            String mesg = "";
//            if (e.getMessage() != null) {
//                mesg += e.getMessage() + " : ";
//            }
//            mesg += e.toString();
//            Throwable ex = e;
//            while (ex.getCause() != null) {
//                ex = ex.getCause();
//                mesg += "\n";
//                if (ex.getMessage() != null) {
//                    mesg += ex.getMessage() + " : ";
//                }
//                mesg += ex.toString();
//            }
//            logger.error("MarshalException " + mesg);
//            LoggingManager.log(logger, mesg, Level.ERROR);
//            throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_NEPTUNE_FILE, mesg);
//
//        }
//        return chouettePTNetworkType;
//    }
//
//    /**
//     * check and return specific charset
//     * <br> if default Neptune charset found : retunr null
//     * <br> if unknown charset found : throw ExchangeRuntimeException
//     * 
//     * @param contentName name for log purpose
//     * @param contentXml xml data to check
//     * @return
//     * @throws IOException 
//     */
//    private String readStream(String contentName, InputStream in) throws IOException {
//        byte bom[] = new byte[BOM_SIZE];
//        String encoding;
//        int unread;
//        PushbackInputStream pushbackStream = new PushbackInputStream(in, 60);
//        int n = pushbackStream.read(bom, 0, bom.length);
//
//        // Read ahead four bytes and check for BOM marks.
//        if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF)) {
//            encoding = "UTF-8";
//            unread = n - 3;
//        } else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
//            encoding = "UTF-16BE";
//            unread = n - 2;
//        } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
//            encoding = "UTF-16LE";
//            unread = n - 2;
//        } else if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {
//            encoding = "UTF-32BE";
//            unread = n - 4;
//        } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {
//            encoding = "UTF-32LE";
//            unread = n - 4;
//        } else {
//            pushbackStream.unread(bom, 0, n);
//            bom = new byte[60];
//            n = pushbackStream.read(bom, 0, bom.length);
//            byte[] array = new byte[n];
//            System.arraycopy(bom, 0, array, 0, n);
//            String header = new String(array);
//            encoding = getCharset(contentName, header);
//            unread = n;
//        }
//
//        // Unread bytes if necessary and skip BOM marks.
//        if (unread > 0) {
//            pushbackStream.unread(bom, (n - unread), unread);
//        } else if (unread < -1) {
//            pushbackStream.unread(bom, 0, 0);
//        }
//
//        // Use given encoding.
//        InputStreamReader reader;
//        if (encoding == null) {
//            reader = new InputStreamReader(pushbackStream);
//        } else {
//            reader = new InputStreamReader(pushbackStream, encoding);
//        }
//        StringBuffer sb = new StringBuffer(300000);
//        while (reader.ready()) {
//            char[] chars = new char[512];
//            int count = reader.read(chars);
//            if (count > 0) {
//                sb.append(chars, 0, count);
//            }
//        }
//        reader.close();
//        logger.info(contentName + " loaded : size = " + sb.length() + ", encoding = " + encoding);
//        return sb.toString();
//    }
//
//    /**
//     * check and return specific charset
//     * <br> if default Neptune charset found : return null
//     * <br> if unknown charset found : throw ExchangeRuntimeException
//     * 
//     * @param contentName name for log purpose
//     * @param contentXml xml data to check
//     * @return
//     */
//    private String getCharset(String contentName, String contentXml) {
//        int startIndex = contentXml.indexOf("encoding=");
//        if (startIndex == -1) {
//            LoggingManager.log(logger, "missing encoding for " + contentName, Level.ERROR);
//            throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_ENCODING, contentName);
//        }
//        startIndex += 10;
//        int endIndex = contentXml.indexOf('"', startIndex);
//        if (endIndex <= 0) {
//            LoggingManager.log(logger, "empty encoding for " + contentName, Level.ERROR);
//            throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_ENCODING, contentName);
//        }
//        String charsetName = contentXml.substring(startIndex, endIndex);
//        try {
//            Charset.forName(charsetName);
//            return charsetName;
//        } catch (Exception e) {
//            LoggingManager.log(logger, "invalid encoding for " + contentName + " : " + charsetName, Level.ERROR);
//            throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_ENCODING, contentName);
//        }
//
//    }
//
//    /**
//     * Check basic XML syntax 
//     * 
//     * @param contentName origin name for logging purpose
//     * @throws ParserConfigurationException invalid syntax
//     * @throws Exception check fails
//     */
//    private void test_xml(String contentName) throws ParserConfigurationException, Exception {
//        logger.debug("Check xml from " + contentName);
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = factory.newDocumentBuilder();
//        builder.parse(contentName);
//        logger.debug("XML content of " + contentName + " is OK ");
//    }

}
