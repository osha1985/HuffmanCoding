
public class HuffmanCode {
    private int value = 0;
    private int length = 0;
    
    
    public HuffmanCode(int value, int length) {
    	this.value = value;
    	this.length = length;
    }
    
    public HuffmanCode() {
		// TODO Auto-generated constructor stub
	}
	public int getLength() {
        return length;
    }
	
	int get(int index) {
        return ( (this.value>>(length - index - 1) & 1) );
    }
    
    public void setValue(int value) {
    	this.value = value;
    }
    
    public int getValue() {
    	return value;
    }
    
    public void setLength(int length) {
    	this.length = length;
    }
    
    public String getCode() {
        String string = "";
        for(int i  = 0; i < this.length; i++) {
            if(this.get(i) == 1) {
                string = string + "1";
            } else {
                string = string + "0";
            }
        }
        return string;
    }
    
    public int hashCode() {
    	int hash = 17;
        hash = hash * 31 + value;
        hash = hash * 31 + length;
    	return hash;
    }
    
    public boolean equals(Object obj) {
    	HuffmanCode code = (HuffmanCode) obj;
    	if(code.value ==  this.value && code.length ==  this.length) {
    		return true;
    	} else {
    		return false;
    	}
    }
}
