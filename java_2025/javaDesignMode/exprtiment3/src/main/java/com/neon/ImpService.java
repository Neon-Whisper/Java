package com.neon;

public class ImpService extends java.rmi.server.UnicastRemoteObject implements
        InterfaceService {

	public ImpService() throws java.rmi.RemoteException {
		super();//
	}

	// }
	/**
	 * 客户端将要调用的方法示例
	 */
	public Object service(Object obj) {

		System.out.println("RMI客户机请求: " + obj);
		return obj.toString();
	}
}
