

class Parameter<T extends Number> {
	private String name;
	private Class<?> clazz;
	
	private T value;
	private int valueIdx;
	private T defaultValue;
	private T[] range;
	
	Parameter(String name, T[] range, int valueIdx, T defaultValue) {
		this.name = name;
		this.range = range;
		setValueIdx(valueIdx);
		clazz = value.getClass();
		this.defaultValue = defaultValue;
		
	}
	
	String getName() {
		return name;
	}
	
	Class<?> getValueClass() {
		return clazz;
	}
	
	T getValue() {
		return value;
	}
	
	int getValueIdx() {
		return valueIdx;
	}
	
	void setValueIdx(int valueIdx) {
		this.valueIdx = valueIdx;
		value = range[valueIdx];
	}
	
	T[] getRange() {
		return range;
	}
	
	
}
