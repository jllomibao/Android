// StockService.aidl
package x40241.jeffrey.lomibao.a4;

// Declare any non-default types here with import statements
import x40241.jeffrey.lomibao.a4.model.StockQuote;
//import x40241.jeffrey.lomibao.a4.model.StockInfo;

interface StockService {
    List<StockQuote> getLatestQuotes();
//    StockInfo getStockInfo(in String symbol);
    int getProcessId();
    void deleteAll();
}
