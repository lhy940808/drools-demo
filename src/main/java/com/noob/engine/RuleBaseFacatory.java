package com.noob.engine;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;

/**
 * ��ʵ��RuleBase���ɹ���
 */
public class RuleBaseFacatory {

	private static class LazyHolder {
		private static final RuleBase INSTANCE = RuleBaseFactory.newRuleBase();
	}

	private RuleBaseFacatory() {
	}

	public static RuleBase getRuleBase() {
		return RuleBaseFacatory.LazyHolder.INSTANCE;
	}

}
