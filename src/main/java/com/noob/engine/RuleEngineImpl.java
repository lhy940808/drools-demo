package com.noob.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.event.DebugWorkingMemoryEventListener;
import org.drools.spi.Activation;

import com.noob.drl.DrlFileCreator;

/**
 * Drools �����ļ���һ������ rule ������ÿ�� rule ������һ������ conditional Ԫ���Լ�Ҫִ�е�һ������
 * consequences �� actions ��ɡ�һ�������ļ��������ж������ 0 ��������import ��������� global �����Լ����
 * function ������
 * 
 * @author xiongwenjun
 *
 */
public class RuleEngineImpl implements IRuleEngine {
	private RuleBase ruleBase = RuleBaseFacatory.getRuleBase();
	private boolean debug = false;

	public void initEngine() {
		try {
			PackageBuilder backageBuilder = getPackageBuilderFromDrlFile(DrlFileCreator.DIRECTORY);
			ruleBase.addPackages(backageBuilder.getPackages());
		} catch (DroolsParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ�����ļ���
	 * 
	 * @return
	 * @throws Exception
	 */
	private PackageBuilder getPackageBuilderFromDrlFile(String directory) throws Exception {
		// װ�ؽű��ļ�
		List<Reader> readers = readRuleFromDrlFile(getDrlFile(directory));
		Properties properties = new Properties();
		properties.load(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("drools.properties"), "UTF-8"));
		PackageBuilder backageBuilder = new PackageBuilder(new PackageBuilderConfiguration(properties));
		for (Reader r : readers) {
			backageBuilder.addPackageFromDrl(r);
		}

		// ���ű��Ƿ�������
		if (backageBuilder.hasErrors()) {
			throw new Exception(backageBuilder.getErrors().toString());
		}

		return backageBuilder;
	}

	/**
	 * @param drlFilePath
	 *            �ű��ļ�·��
	 * @return
	 * @throws FileNotFoundException
	 */
	private List<Reader> readRuleFromDrlFile(File[] files) throws Exception {
		if (null == files || 0 == files.length) {
			return null;
		}

		List<Reader> readers = new ArrayList<Reader>();

		for (File file : files) {
			readers.add(new InputStreamReader(new FileInputStream(file)));
		}

		return readers;
	}

	/**
	 * ��ȡ���Թ����ļ�
	 * 
	 * @return
	 */
	private File[] getDrlFile(String directory) {
		File[] drlFiles = null;
		File dir = new File(directory);
		if (dir.exists() && dir.isDirectory()) {
			drlFiles = dir.listFiles((a, b) -> b.endsWith(".drl"));

		}

		return drlFiles;
	}

	public void refreshEnginRule() {
		org.drools.rule.Package[] packages = ruleBase.getPackages();
		for (org.drools.rule.Package pg : packages) {
			ruleBase.removePackage(pg.getName());
		}

		initEngine();
	}

	public <T> void executeRuleEngine(final T t) {
		executeRuleEngine(m -> {
			// ����ڶ�������ֵʱ����Ҫ�ù�����������δ ����֪ʶ�Ķ�����Ӧʹ�� WorkingMemory ��� setGlobal() ����
			// m.setGlobal("testDAO", null);
			m.insert(t);
		});

	}

	public <T> void executeRuleEngine(IWorkingEnvironmentCallback environment) {
		if (null == ruleBase.getPackages() || 0 == ruleBase.getPackages().length) {
			return;
		}
		StatefulSession workingMemory = ruleBase.newStatefulSession();
		if (debug) {
			workingMemory.addEventListener(new DebugWorkingMemoryEventListener());

		}
		environment.initEnvironment(workingMemory);
		workingMemory.fireAllRules(new org.drools.spi.AgendaFilter() {
			public boolean accept(Activation activation) {
				return !activation.getRule().getName().contains("_test");
			}
		});
		workingMemory.dispose();

	}

}
