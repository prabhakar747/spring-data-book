package com.oreilly.springdata.hadoop.hive;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.hadoop.hive.service.HiveClient;
import org.apache.hadoop.hive.service.HiveServerException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class HivePasswordRepository implements PasswordRepository {

	private static final Log logger = LogFactory.getLog(HivePasswordRepository.class);

	private @Value("${hive.table}")	String tableName;
	
	private @Value("${hive.host}")	String host;
	
	private @Value("${hive.port}")	int port;

	public Long count() {	
		HiveClient hiveClient = createHiveClient();		
		try {
			hiveClient.execute("select count(*) from " + tableName);
			return Long.parseLong(hiveClient.fetchOne());
			// checked exceptions
		} catch (HiveServerException ex) {
			throw translateExcpetion(ex);
		} catch (org.apache.thrift.TException tex) {
			throw translateExcpetion(tex);
		} finally {
			try {
				hiveClient.shutdown();
			} catch (org.apache.thrift.TException tex) {
				logger.debug("Unexpected exception on shutting down HiveClient", tex);
			}
		}
	}

	protected HiveClient createHiveClient()  {
		TSocket transport = new TSocket(host, port);
		HiveClient hive = new HiveClient(new TBinaryProtocol(transport));
		try {
			transport.open();
		} catch (TTransportException e) {
			throw translateExcpetion(e);
		}
		return hive;
	}

	private RuntimeException translateExcpetion(Exception ex) {
		return new RuntimeException(ex);
	}

}
