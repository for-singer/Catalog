package catalog.service;

import catalog.helper.XmlTagHelper;
import catalog.model.CompactDisk;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

@Service
public class XmlService {
    @Autowired
    WriteXmlService writeXmlService;
    
    private static String realPath;
    private static final String FILE_PATH = "\\downloads\\";
    private static final String RESULT_FILE_NAME = "result.xml";
    private static final String UPLOADED_FILE_NAME = "uploaded.xml";
    private static final String TEMP_FILE_NAME = "temp.xml";
    private static final String XSD_FILE_NAME = "schema.xsd"; 
    
    private static final int DOWNLOAD_BUFFER_SIZE = 4096;
    
    private final String XSD_FILE = FILE_PATH + XSD_FILE_NAME;
    private final String UPLOADED_FILE = FILE_PATH + UPLOADED_FILE_NAME;
    private final String RESULT_FILE = FILE_PATH + RESULT_FILE_NAME;
    private final String TEMP_FILE = FILE_PATH + TEMP_FILE_NAME;
    
    public String upload(HttpServletRequest request, MultipartFile file) {
        String result = new String();
        
        ServletContext context = request.getServletContext();
        realPath = context.getRealPath("");
        
        if (!file.isEmpty()) {
            try {                
                byte[] bytes = file.getBytes();
                
                try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(realPath + UPLOADED_FILE));) {                
                    stream.write(bytes);
                }
                
                if (validateUploaded()) {
                    result = "You successfully uploaded file!";
                    updateCatalogFile();
                }
                else {
                    result = "The uploaded file is not valid and can't be processed!";
                }
            }
            catch (Exception e) {
                result = "You failed to upload file => " + e.getMessage();
            }
        } else {
            result = "You failed to upload file because the file was empty.";
        }
        return result;
    }
    
    public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletContext context = request.getServletContext();
        realPath = context.getRealPath("");
 
        try (FileInputStream inputStream = new FileInputStream(realPath + RESULT_FILE)) {
         
            String mimeType = context.getMimeType((realPath + RESULT_FILE));
            if (mimeType == null) {
                mimeType = "application/xml";
            }

            response.setContentType(mimeType);
            response.setContentLength((int) new File(realPath + RESULT_FILE).length());

            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"",
                    new File(realPath + RESULT_FILE).getName());
            response.setHeader(headerKey, headerValue);

            try (OutputStream outStream = response.getOutputStream()) {

                byte[] buffer = new byte[DOWNLOAD_BUFFER_SIZE];
                int bytesRead = -1;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            }
        
        }
    }
    
    public List<CompactDisk> getList(String realPath) {
        List<CompactDisk> cdList = new ArrayList();
        
        boolean bTitle = false;
        boolean bArtist = false;
        boolean bCountry = false;
        boolean bCompany = false;
        boolean bPrice = false;
        boolean bYear = false;
        
        CompactDisk cd = null;
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new FileInputStream(realPath + RESULT_FILE));
            int event = xmlStreamReader.getEventType();
            while (true) {
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        if (xmlStreamReader.getLocalName().equals(XmlTagHelper.CD)) {
                            cd = new CompactDisk();
                        } else if (xmlStreamReader.getLocalName().equals(XmlTagHelper.TITLE)) {
                            bTitle = true;
                        } else if (xmlStreamReader.getLocalName().equals(XmlTagHelper.ARTIST)) {
                            bArtist = true;
                        } else if (xmlStreamReader.getLocalName().equals(XmlTagHelper.COUNTRY)) {
                            bCountry = true;
                        } else if (xmlStreamReader.getLocalName().equals(XmlTagHelper.COMPANY)) {
                            bCompany = true;
                        } else if (xmlStreamReader.getLocalName().equals(XmlTagHelper.PRICE)) {
                            bPrice = true;
                        } else if (xmlStreamReader.getLocalName().equals(XmlTagHelper.YEAR)) {
                            bYear = true;
                        }
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        if (bTitle) {
                            cd.setTitle(xmlStreamReader.getText());
                            bTitle = false;
                        } else if (bArtist) {
                            cd.setArtist((xmlStreamReader.getText()));
                            bArtist = false;
                        } else if (bCountry) {
                            cd.setCountry(xmlStreamReader.getText());
                            bCountry = false;
                        } else if (bCompany) {
                            cd.setCompany(xmlStreamReader.getText());
                            bCompany = false;
                        } else if (bPrice) {
                            cd.setPrice(xmlStreamReader.getText());
                            bPrice = false;
                        } else if (bYear) {
                            cd.setYear(xmlStreamReader.getText());
                            bYear = false;
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        if (xmlStreamReader.getLocalName().equals(XmlTagHelper.CD)) {
                            cdList.add(cd);
                        }
                        break;
                }
                if (!xmlStreamReader.hasNext()) {
                    break;
                }
                event = xmlStreamReader.next();
            }

        } catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        }
        
        return cdList;
    }
    
    private boolean validateUploaded() {
         
        try {
            SchemaFactory factory = 
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(realPath + XSD_FILE));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(realPath + UPLOADED_FILE));
        }
        catch (IOException | SAXException e) {
            System.out.println("Exception: "+e.getMessage());
            return false;
        }
        
        return true;
    }
    
    private void updateResultsFile() throws Exception {
        FileReader fr = null;
        XMLStreamReader resultFileReader = null;
        
        try {        
            XMLInputFactory inFactory = XMLInputFactory.newInstance();
            fr = new FileReader(realPath + RESULT_FILE);
            resultFileReader = inFactory.createXMLStreamReader(fr);

            CompactDisk compactDisk = new CompactDisk();
            String tagContent = null;

            boolean updateOriginalDisk = false;

            while(resultFileReader.hasNext()) {
                int event = resultFileReader.next();

                switch(event) {
                    case XMLStreamConstants.START_ELEMENT: 
                        if (XmlTagHelper.CD.equals(resultFileReader.getLocalName())) {
                            compactDisk = new CompactDisk();
                            updateOriginalDisk = false; 
                        }
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        tagContent = resultFileReader.getText().trim();
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        switch(resultFileReader.getLocalName()) {
                            case XmlTagHelper.CD:
                                writeXmlService.write(compactDisk);
                                break;
                            case XmlTagHelper.TITLE:
                                CompactDisk cdFromUploadedFile = getCDByTitleFromFile(tagContent, realPath + UPLOADED_FILE);  
                                if (cdFromUploadedFile  == null) {                                
                                    compactDisk.setTitle(tagContent);
                                }
                                else {
                                    updateOriginalDisk = true;
                                    compactDisk = cdFromUploadedFile;
                                }
                                break;
                            case XmlTagHelper.ARTIST:
                                if (!updateOriginalDisk) {
                                    compactDisk.setArtist(tagContent);
                                }
                                break;
                            case XmlTagHelper.COUNTRY:
                                if (!updateOriginalDisk) {
                                    compactDisk.setCountry(tagContent);
                                }
                                break;    
                            case XmlTagHelper.COMPANY:
                                if (!updateOriginalDisk) {
                                    compactDisk.setCompany(tagContent);
                                }
                                break;
                            case XmlTagHelper.PRICE:
                                if (!updateOriginalDisk) {
                                    compactDisk.setPrice(tagContent);
                                }  
                                break;
                            case XmlTagHelper.YEAR:
                                if (!updateOriginalDisk) {
                                    compactDisk.setYear(tagContent);
                                }
                                break;                              
                        }
                        break;
                }
            }
        }
        finally {
            if (resultFileReader != null) {
                resultFileReader.close();
            }
            if (fr != null) {
                fr.close();
            }
        }
    }
    
    private void updateResultsFileAddingNewCd() throws Exception {
        FileReader fr = null;
        XMLStreamReader resultFileReader = null;
        
        try {
            XMLInputFactory inFactory = XMLInputFactory.newInstance();
            fr = new FileReader(realPath + UPLOADED_FILE);
            resultFileReader = inFactory.createXMLStreamReader(fr);

            CompactDisk compactDisk = null;
            String tagContent = null;

            boolean updateOriginalDisk = false;

            while(resultFileReader.hasNext()) {
                int event = resultFileReader.next();

                switch(event) {
                    case XMLStreamConstants.START_ELEMENT: 
                        if (XmlTagHelper.CD.equals(resultFileReader.getLocalName())) {
                            compactDisk = new CompactDisk();
                            updateOriginalDisk = false; 
                        }
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        tagContent = resultFileReader.getText().trim();
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        switch(resultFileReader.getLocalName()) {
                            case XmlTagHelper.CD:
                                if (!updateOriginalDisk) { 
                                    writeXmlService.write(compactDisk);
                                }
                                break;
                            case XmlTagHelper.TITLE:
                                CompactDisk cd = getCDByTitleFromFile(tagContent, realPath + RESULT_FILE);  
                                if (cd  == null) {                                
                                    compactDisk.setTitle(tagContent);
                                }
                                else {
                                    updateOriginalDisk = true;
                                }
                                break;
                            case XmlTagHelper.ARTIST:
                                if (!updateOriginalDisk) {
                                    compactDisk.setArtist(tagContent);
                                }
                                break;
                            case XmlTagHelper.COUNTRY:
                                if (!updateOriginalDisk) {
                                    compactDisk.setCountry(tagContent);
                                }
                                break;    
                            case XmlTagHelper.COMPANY:
                                if (!updateOriginalDisk) {
                                    compactDisk.setCompany(tagContent);
                                }
                                break;
                            case XmlTagHelper.PRICE:
                                if (!updateOriginalDisk) {
                                    compactDisk.setPrice(tagContent);
                                }  
                                break;
                            case XmlTagHelper.YEAR:
                                if (!updateOriginalDisk) {
                                    compactDisk.setYear(tagContent);
                                }
                                break;
                        }
                        break;
                }
            }
        }
        finally {
            if (resultFileReader != null) {
                resultFileReader.close();
            }
            if (fr != null) {
                fr.close();
            }            
        }
    }

    public boolean updateCatalogFile() {
        File resultFile = new File(realPath + RESULT_FILE);
        File uploadedFile = new File(realPath + UPLOADED_FILE);
        File tempFile = new File(realPath + TEMP_FILE);              
        
        if(!resultFile.exists()) {
            
            if(uploadedFile.renameTo(resultFile)) {
                uploadedFile.delete();
                return true;
            }
            else {
                return false;
            }
        }
        else {
            try {
                writeXmlService.startDocument(tempFile);
                
                updateResultsFile();                
                updateResultsFileAddingNewCd();
                
                writeXmlService.endDocument();
                
                Files.copy(tempFile.toPath(), resultFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING); 
                uploadedFile.delete();
                tempFile.delete();
                
                return true;
            } 
            catch (Exception e) {
               e.printStackTrace();
            }            
        }
        return false;
    }
    
    private CompactDisk getCDByTitleFromFile(String title, String file) {
        try {                
            XMLInputFactory inFactory = XMLInputFactory.newInstance();
            FileReader fr = new FileReader(file);
            XMLStreamReader resultFileReader = inFactory.createXMLStreamReader(fr);

            CompactDisk compactDisk = null;
            String tagContent = null;

            boolean updateCD = false;

            while(resultFileReader.hasNext()) {
                int event = resultFileReader.next();

                switch(event) {
                    case XMLStreamConstants.START_ELEMENT:
                        if(XmlTagHelper.CD.equals(resultFileReader.getLocalName())) {
                            updateCD = false;
                        }
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        tagContent = resultFileReader.getText().trim();
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        switch(resultFileReader.getLocalName()) {
                            case XmlTagHelper.TITLE:
                                if (tagContent.equals(title)) {
                                    updateCD = true;
                                    compactDisk = new CompactDisk();
                                    compactDisk.setTitle(tagContent);
                                }
                                break;
                            case XmlTagHelper.ARTIST:
                                if (updateCD) {
                                    compactDisk.setArtist(tagContent);
                                }
                                break;    
                            case XmlTagHelper.COUNTRY:
                                if (updateCD) {
                                    compactDisk.setCountry(tagContent);
                                }
                                break;
                            case XmlTagHelper.COMPANY:
                                if (updateCD) {  
                                    compactDisk.setCompany(tagContent);
                                }
                                break;
                            case XmlTagHelper.PRICE:
                                if (updateCD) {  
                                    compactDisk.setPrice(tagContent);
                                }
                                break;
                            case XmlTagHelper.YEAR:
                                if (updateCD) {  
                                    compactDisk.setYear(tagContent);
                                }
                                break;
                        }
                    break;                     
                }
            }                

            return compactDisk;                                
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
