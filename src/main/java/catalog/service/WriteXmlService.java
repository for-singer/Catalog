package catalog.service;

import catalog.helper.XmlTagHelper;
import catalog.model.CompactDisk;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.springframework.stereotype.Service;

@Service
public class WriteXmlService {
    private FileWriter fileWriter = null;
    private XMLStreamWriter xmlWriter = null;
    
    public void startDocument(File file) throws IOException {
        try {
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            
            fileWriter = new FileWriter(file);
            
            xmlWriter = factory.createXMLStreamWriter(fileWriter);

            xmlWriter.writeStartDocument();
            xmlWriter.writeCharacters("\n");
            xmlWriter.writeStartElement(XmlTagHelper.CATALOG);

            xmlWriter.flush();
        }
        catch (XMLStreamException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void endDocument() {
        try {
            xmlWriter.writeCharacters("\n");
            xmlWriter.writeEndElement();
            xmlWriter.writeEndDocument();
            
            xmlWriter.flush();
            xmlWriter.close();
            
            fileWriter.close();
        }
        catch (XMLStreamException e) {
            e.printStackTrace();
        }    
        catch (IOException e) {
            e.printStackTrace();
        }    
    }
    
    public void write(CompactDisk compactDisk) {
        try {
            xmlWriter.writeCharacters("\n");
            xmlWriter.writeStartElement(XmlTagHelper.CD);
            
            writeTag(XmlTagHelper.TITLE, compactDisk.getTitle());
            writeTag(XmlTagHelper.ARTIST, compactDisk.getArtist());
            writeTag(XmlTagHelper.COUNTRY, compactDisk.getCountry());
            writeTag(XmlTagHelper.COMPANY, compactDisk.getCompany());
            writeTag(XmlTagHelper.PRICE, compactDisk.getPrice());
            writeTag(XmlTagHelper.YEAR, compactDisk.getYear());
            
            xmlWriter.writeCharacters("\n");
            xmlWriter.writeEndElement();

            xmlWriter.flush();

        } 
        catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }
    
    private void writeTag(String tagName, String tagValue) {
        try {
            xmlWriter.writeCharacters("\n");
            xmlWriter.writeStartElement(tagName);
            xmlWriter.writeCharacters(tagValue);
            xmlWriter.writeEndElement();
        } 
        catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }
    
}
