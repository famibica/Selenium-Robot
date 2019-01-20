package com.sap.amd.rmi;

import java.io.Serializable;
import java.rmi.RemoteException;

import com.sap.amd.utils.exceptions.SessionUnlockerException;

public class SessionUnlocker implements Runnable, Serializable
{
	private static final long serialVersionUID = 3689951758881191074L;
	
	private int delay;
	private Wrapper provider;

	public SessionUnlocker(Wrapper provider, int delay) throws SessionUnlockerException
	{
//		try
//		{
//			Registry registry = LocateRegistry.getRegistry();
//			this.provider = (Wrapper) registry.lookup(name == null ? "AMD" : "AMD:" + name);
//		}
//		catch (Exception e)
//		{
//			throw new SessionUnlockerException(name);
//		}
		
		this.provider = provider;		
		this.delay = delay;
	}

	@Override
	public void run()
	{
		try
		{
			while (Server.isBound(provider.getUser()))
			{
				provider.unlock();

				try
				{
					Thread.sleep(delay * 1000);
				}
				catch (InterruptedException e) {}
			}
		}
		catch (RemoteException e) {}
	}
}
