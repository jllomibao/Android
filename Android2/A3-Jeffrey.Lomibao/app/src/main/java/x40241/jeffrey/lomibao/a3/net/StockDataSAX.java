package x40241.jeffrey.lomibao.a3.net;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import x40241.jeffrey.lomibao.a3.model.StockQuote;

/**
 * Utility to parse Stocks data in the form:
 *   <Stocks sequence="1298061395759">
 *   <stock>
 *     <name>Yahoo! Inc.</name>
 *     <symbol>YHOO</symbol>
 *     <price>17.60</price>
 *   </stock>
 *   <stock>
 *     <name>Pfizer Inc.</name>
 *     <symbol>PFE</symbol>
 *     <price>18.77</price>
 *   </stock>
 *   </Stocks>
 * 
 * ...into a List of StockQuote objects.
 * 
 * @author Jeffrey Peacock (Jeffrey.Peacock@uci.edu)
 */
public final class StockDataSAX
	extends DefaultHandler
{
    private static final String LOGTAG = "StockQuoteSAX";
    private static final boolean DEBUG = false;
    
    private static final String DOC_START_END_ELEMENT   = "Stocks";
    private static final String DATA_START_END_ELEMENT  = "stock";
    private static final String SEQUENCE_ATTRIBUTE_NAME = "sequence";
    
    private long sequence;
	private StockQuote stockQuote;
	private List<StockQuote> list = new ArrayList<StockQuote>();
    private StringBuilder buffer = new StringBuilder();

    public StockDataSAX() {}
    
    public List<StockQuote> parse (byte[] bytes) throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        parse(is);
        is.close();
        return list;
    }
    
    public List<StockQuote> parse (InputStream is) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(is, this);
        return list;
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startDocument()
     */
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endDocument()
     */
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement (String uri, String localName, String name, Attributes attributes)
        throws SAXException
    {
        if (DEBUG) Log.d (LOGTAG, "startElement="+localName);
        super.startElement (uri, localName, name, attributes);
        if (localName.equals(DOC_START_END_ELEMENT)) {
            String value = attributes.getValue(SEQUENCE_ATTRIBUTE_NAME);
            if (DEBUG) Log.d (LOGTAG, SEQUENCE_ATTRIBUTE_NAME+"="+value);
            sequence = Long.parseLong(value);
            return;
        }
        if (localName.equals(DATA_START_END_ELEMENT)) {
            stockQuote = new StockQuote();
            stockQuote.setSequence(sequence);
        }
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters (char[] ch, int start, int length)
        throws SAXException
    {
        super.characters(ch, start, length);
        buffer.append(ch, start, length);
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement (String uri, String localName, String name)
        throws SAXException
    {
        if (DEBUG) Log.d (LOGTAG, "endElement="+localName);
        super.endElement(uri, localName, name);
        if (localName.equals(DOC_START_END_ELEMENT)) {
            if (DEBUG) Log.d (LOGTAG, "***  END OF DOC");
            return;
        }
        if (localName.equals(DATA_START_END_ELEMENT)) {
            if (DEBUG) Log.d (LOGTAG, "***  END OF DATA ITEM");
            list.add(stockQuote);
            stockQuote = null;
            return;
        }
        parseEndElement (uri, localName, name, buffer.toString());
        buffer.delete(0, buffer.length());
    }
    
    //  Encapsulate actual parsing specifics here.
    private void parseEndElement (String uri, String localName, String name, String value)
    {
        value = value.trim();  //  may not be appropriate for all parsing situations
        if (DEBUG) {
            Log.d (LOGTAG, "localName="+localName);
            Log.d (LOGTAG, "value="+value);
        }
        if (localName.equals("name")) {
            stockQuote.setName(value);
            return;
        }
        if (localName.equals("symbol")) {
            stockQuote.setSymbol(value);
            return;
        }
        if (localName.equals("price")) {
            stockQuote.setPrice(Float.parseFloat(value));
            return;
        }
    }
}
