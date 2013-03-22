import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

final class Leader extends PlayerImpl
{
	private Leader() throws RemoteException, NotBoundException
	{
		super(PlayerType.LEADER, "Simple Leader");
	}

	@Override
	public void proceedNewDay(int p_date) throws RemoteException
	{
		m_platformStub.publishPrice(m_type, 1.0f);
	}

	public static void main(final String[] p_args)
		throws RemoteException, NotBoundException
	{
		new Leader();
	}

}
