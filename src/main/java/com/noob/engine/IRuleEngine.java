package com.noob.engine;

/**
 * ��������
 *
 */
public interface IRuleEngine {
	/** 
     * ��ʼ���������� 
     */  
    public void initEngine();

	/** 
     * ˢ�¹��������еĹ��� 
     */  
    public void refreshEnginRule();

	/** 
     * ִ�й������� 
     * @param ����Fact 
     */  
    public <T> void executeRuleEngine(final T fact);
}
