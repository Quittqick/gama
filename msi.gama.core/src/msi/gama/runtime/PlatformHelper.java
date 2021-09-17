/*******************************************************************************************************
 *
 * PlatformHelper.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package msi.gama.runtime;

import org.eclipse.core.runtime.Platform;

/**
 * The Class PlatformHelper.
 */
public class PlatformHelper {

	/** The platform string. */
	private static String platformString = Platform.getOS();

	/** The is windows. */
	private static boolean isWindows = "win32".equals(platformString);

	/** The is mac. */
	private static boolean isMac = "macosx".equals(platformString);

	/** The is linux. */
	private static boolean isLinux = "linux".equals(platformString);

	/** The is developer. */
	private static Boolean isDeveloper;

	/**
	 * Instantiates a new platform helper.
	 */
	private PlatformHelper() {}

	/**
	 * Checks if is windows.
	 *
	 * @return true, if is windows
	 */
	public static boolean isWindows() { return isWindows; }

	/**
	 * Checks if is linux.
	 *
	 * @return true, if is linux
	 */
	public static boolean isLinux() { return isLinux; }

	/**
	 * Checks if is mac.
	 *
	 * @return true, if is mac
	 */
	public static boolean isMac() { return isMac; }

	/**
	 * Checks if is developer.
	 *
	 * @return true, if is developer
	 */
	public static boolean isDeveloper() { // NO_UCD (unused code)
		if (isDeveloper == null) {
			isDeveloper = Platform.getInstallLocation() == null
					|| Platform.getInstallLocation().getURL().getPath().contains("org.eclipse.pde.core");
		}
		return isDeveloper;
	}

	/**
	 * The JAVA version
	 */
	public static final int JAVA_VERSION; // NO_UCD (unused code)
	static {
		JAVA_VERSION = parseVersion(System.getProperty("java.version")); //$NON-NLS-1$
	}

	/**
	 * Parses the version.
	 *
	 * @param version
	 *            the version
	 * @return the int
	 */
	private static int parseVersion(final String version) {
		if (version == null) return 0;
		int major = 0, minor = 0, micro = 0;
		final int length = version.length();
		int index = 0, start = 0;
		while (index < length && Character.isDigit(version.charAt(index))) { index++; }
		try {
			if (start < length) { major = Integer.parseInt(version.substring(start, index)); }
		} catch (final NumberFormatException e) {}
		start = ++index;
		while (index < length && Character.isDigit(version.charAt(index))) { index++; }
		try {
			if (start < length) { minor = Integer.parseInt(version.substring(start, index)); }
		} catch (final NumberFormatException e) {}
		start = ++index;
		while (index < length && Character.isDigit(version.charAt(index))) { index++; }
		try {
			if (start < length) { micro = Integer.parseInt(version.substring(start, index)); }
		} catch (final NumberFormatException e) {}
		return javaVersion(major, minor, micro);
	}

	/**
	 * Returns the Java version number as an integer.
	 *
	 * @param major
	 * @param minor
	 * @param micro
	 * @return the version
	 */
	public static int javaVersion(final int major, final int minor, final int micro) {
		return (major << 16) + (minor << 8) + micro;
	}

}
