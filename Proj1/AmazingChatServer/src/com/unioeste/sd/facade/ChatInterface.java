package com.unioeste.sd.facade;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatInterface extends Remote{
	public void login(UserInterface user) throws RemoteException;
	public void logout(UserInterface user) throws RemoteException;
	public void sendBroadcastMessage(MessageInterface message) throws RemoteException;
	public void sendUnicastMessage(UserInterface target, MessageInterface message) throws RemoteException;
	public UserInterface[] getLoggedUsers() throws RemoteException;
	public void notifyChange() throws RemoteException;
}
