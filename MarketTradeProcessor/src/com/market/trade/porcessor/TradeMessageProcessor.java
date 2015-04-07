package com.market.trade.porcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;

public class TradeMessageProcessor {
	public TradeMessageProcessor() {
	}
	
	protected static synchronized void writeMessage(String message){

		 try {
			 File file = new File("tradeMessageOut.xml");
		     if(!file.exists()){
		    	 createXML("tradeMessageOut.xml");
		     }
		     XMLInputFactory inFactory = XMLInputFactory.newInstance();
		     XMLEventReader eventReader = inFactory.createXMLEventReader(new FileInputStream(file));
		     XMLOutputFactory factory = XMLOutputFactory.newInstance();
		     XMLEventWriter writer = factory.createXMLEventWriter(new FileWriter(file));
		     XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		     
		     while (eventReader.hasNext()) {
		         XMLEvent event = eventReader.nextEvent();
		         writer.add(event);
		         if (event.getEventType() == XMLEvent.START_ELEMENT)  {
		        	 if(event.asStartElement().getName().toString().equalsIgnoreCase("TRADE_MESSAGES")){
		                 writer.add(eventFactory.createStartElement("", null, "MESSAGE"));
		                 writer.add(eventFactory.createStartElement("", null, "DATE"));
		                 writer.add(eventFactory.createCharacters(String.valueOf(System.currentTimeMillis())));
		                 writer.add(eventFactory.createEndElement("", null, "DATE"));
		                 
		                 writer.add(eventFactory.createStartElement("", null, "INFO"));
		                 writer.add(eventFactory.createCharacters(message));
		                 writer.add(eventFactory.createEndElement("", null, "INFO"));
		                 
		                 writer.add(eventFactory.createEndElement("", null, "MESSAGE"));
		        	 }
		         }
		     }
		     writer.flush();
		     writer.close();
		 } catch (XMLStreamException e) {
		     e.printStackTrace();
		 } catch (IOException e) {
		     e.printStackTrace();
		 }
	}
	
	private static void createXML(String fileName) {
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		 try {
		     XMLStreamWriter writer = factory.createXMLStreamWriter(
		             new FileWriter(fileName));
		     writer.writeStartDocument("UTF-8", "1.0");
		     writer.writeStartElement("TRADE_MESSAGES");
		     writer.writeEndElement();
		     writer.writeEndDocument();
		     writer.flush();
		     writer.close();
		 } catch (XMLStreamException e) {
		     e.printStackTrace();
		 } catch (IOException e) {
		     e.printStackTrace();
		 }		
	}
}