package protocol3.backend;

public class Utilities
{
	public static String calculateTime(double seconds)
	{
		long hours = (long) (seconds / 3600);
		long hoursRem = (long) (seconds % 3600);
		long minutes = hoursRem / 60;

		String hoursString = "";
		String minutesString = "";

		if (hours == 1)
		{
			hoursString = hours + " hour";
		} else
		{
			hoursString = hours + " hours";
		}

		if (minutes == 1)
		{
			minutesString = minutes + " minute";
		} else if (minutes == 0)
		{
			minutesString = "";
		} else
		{
			minutesString = minutes + " minutes";
		}

		if (minutesString == "" && hoursString == "")
		{
			return "None";
		}

		if (minutes == 0)
		{
			return hoursString;
		}

		else
		{
			return hoursString + ", " + minutesString;
		}
	}
}
