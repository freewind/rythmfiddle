package demo;

import com.greenlaw110.rythm.extension.Transformer;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.utils.S;

public class Order {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(int itemNumber) {
        this.itemNumber = itemNumber;
    }
    
    public int getTotalPrice() {
        return itemNumber * unitPrice;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
    
    @Transformer(requireTemplate = true)
    public static String asCurrency(Integer amount) {
        return asCurrency(null, amount);
    }
    
    public static String asCurrency(ITemplate template, Integer amount) {
        Double d = (float)amount / 100.00;
        return S.formatCurrency(template, d, null, null);
    }

    private String id;
    private String description;
    private int unitPrice;
    private int itemNumber;
    private boolean closed;
}
