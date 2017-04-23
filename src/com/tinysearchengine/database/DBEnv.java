package com.tinysearchengine.database;

import java.io.File;

import org.apache.log4j.Logger;

import com.sleepycat.je.*;
import com.sleepycat.persist.*;
import com.sleepycat.persist.model.AnnotationModel;

public class DBEnv {

	private static final String k_STORENAME = "TINYSEARCHENGINE.STORE";

	private Environment d_environment;
	private EntityStore d_store;

	public DBEnv(File envRoot) {
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setTransactional(true);

		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		envConfig.setReadOnly(false);

		AnnotationModel model = new AnnotationModel();
		model.registerClass(PairPersistentProxy.class);
		model.registerClass(ImmutablePairPersistentProxy.class);
		model.registerClass(URLPersistentProxy.class);

		StoreConfig storeConfig = new StoreConfig();
		storeConfig.setModel(model);
		storeConfig.setAllowCreate(true);
		storeConfig.setDeferredWrite(true);
		storeConfig.setReadOnly(false);

		d_environment = new Environment(envRoot, envConfig);
		d_store = new EntityStore(d_environment, k_STORENAME, storeConfig);
	}
	
	public Environment getEnvironment() {
		return d_environment;
	}
	
	public EntityStore getStore() {
		return d_store;
	}
	
	public void close() {
		try {
			d_store.close();
			d_environment.close();
		} catch (DatabaseException e) {
			Logger logger = Logger.getLogger(DBEnv.class);
			logger.info("DBException", e);
		}
	}
}
