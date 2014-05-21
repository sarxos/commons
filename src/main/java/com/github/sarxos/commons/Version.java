package com.github.sarxos.commons;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.StringTokenizer;


public final class Version implements Serializable, Comparable<Version> {

	private static final long serialVersionUID = -3054349171116917643L;

	/**
	 * Version identifier parts separator.
	 */
	public static final char SEPARATOR = '.';

	/**
	 * Major version
	 */
	private transient short major;

	/**
	 * Minor version
	 */
	private transient short minor;

	/**
	 * Build number
	 */
	private transient short build;

	/**
	 * Build name
	 */
	private transient short name;

	private Version() {
		// no-op
	}

	/**
	 * Parses given string as version identifier. All missing parts will be
	 * initialized to 0 or empty string. Parsing starts from left side of the
	 * string.
	 * 
	 * @param str version identifier as string
	 * @return version identifier object
	 */
	public static Version parse(final String str) {
		Version result = new Version();
		result.parseString(str);
		return result;
	}

	/**
	 * Parse version string into this object fields.
	 */
	private void parseString(final String str) {

		major = 0;
		minor = 0;
		build = 0;
		name = 0;

		StringTokenizer st = new StringTokenizer(str, "" + SEPARATOR, false);

		// major segment
		if (!st.hasMoreTokens()) {
			return;
		}
		String token = st.nextToken();
		major = Short.parseShort(token, 10);

		// minor segment
		if (!st.hasMoreTokens()) {
			return;
		}

		token = st.nextToken();
		minor = Short.parseShort(token, 10);

		// build segment
		if (!st.hasMoreTokens()) {
			return;
		}

		token = st.nextToken();
		build = Short.parseShort(token, 10);

		// name segment
		if (!st.hasMoreTokens()) {
			return;
		}

		token = st.nextToken();
		name = Short.parseShort(token, 10);
	}

	/**
	 * Creates version identifier object from given parts. No validation
	 * performed during object instantiation, all values become parts of version
	 * identifier as they are.
	 * 
	 * @param aMajor major version number
	 * @param aMinor minor version number
	 * @param aBuild build number
	 * @param aName build name, <code>null</code> value becomes empty string
	 */
	public Version(final int aMajor, final int aMinor, final int aBuild, final int aName) {
		major = (short) aMajor;
		minor = (short) aMinor;
		build = (short) aBuild;
		name = (short) aName;
	}

	public Version(final long version) {
		parseLong(version);
	}

	private void parseLong(final long version) {
		ByteBuffer bb = ByteBuffer.allocate(8).putLong(version);
		bb.rewind();
		major = bb.getShort();
		minor = bb.getShort();
		build = bb.getShort();
		name = bb.getShort();
	}

	public Version(String version) {
		parseString(version);
	}

	/**
	 * @return build number
	 */
	public int getBuild() {
		return build;
	}

	/**
	 * @return major version number
	 */
	public int getMajor() {
		return major;
	}

	/**
	 * @return minor version number
	 */
	public int getMinor() {
		return minor;
	}

	/**
	 * @return build name
	 */
	public int getName() {
		return name;
	}

	/**
	 * Compares two version identifiers to see if this one is greater than or
	 * equal to the argument.
	 * <p>
	 * A version identifier is considered to be greater than or equal if its
	 * major component is greater than the argument major component, or the
	 * major components are equal and its minor component is greater than the
	 * argument minor component, or the major and minor components are equal and
	 * its build component is greater than the argument build component, or all
	 * components are equal.
	 * </p>
	 * 
	 * @param o the other version identifier
	 * @return <code>true</code> if this version identifier is compatible with
	 *         the given version identifier, and <code>false</code> otherwise
	 */
	public boolean isGreaterOrEqualTo(final Version o) {
		if (o == null) {
			return false;
		}
		if (major > o.major) {
			return true;
		}
		if (major == o.major && minor > o.minor) {
			return true;
		}
		if (major == o.major && minor == o.minor && build > o.build) {
			return true;
		}
		if (major == o.major && minor == o.minor && build == o.build && name == o.name) {
			return true;
		}
		return false;
	}

	/**
	 * Compares two version identifiers for compatibility.
	 * <p>
	 * A version identifier is considered to be compatible if its major
	 * component equals to the argument major component, and its minor component
	 * is greater than or equal to the argument minor component. If the minor
	 * components are equal, than the build component of the version identifier
	 * must be greater than or equal to the build component of the argument
	 * identifier.
	 * </p>
	 * 
	 * @param other the other version identifier
	 * @return <code>true</code> if this version identifier is compatible with
	 *         the given version identifier, and <code>false</code> otherwise
	 */
	public boolean isCompatibleWith(final Version other) {
		if (other == null) {
			return false;
		}
		if (major != other.major) {
			return false;
		}
		if (minor > other.minor) {
			return true;
		}
		if (minor < other.minor) {
			return false;
		}
		if (build >= other.build) {
			return true;
		}
		return false;
	}

	/**
	 * Compares two version identifiers for equivalency.
	 * <p>
	 * Two version identifiers are considered to be equivalent if their major
	 * and minor components equal and are at least at the same build level as
	 * the argument.
	 * </p>
	 * 
	 * @param other the other version identifier
	 * @return <code>true</code> if this version identifier is equivalent to the
	 *         given version identifier, and <code>false</code> otherwise
	 */
	public boolean isEquivalentTo(final Version other) {
		if (other == null) {
			return false;
		}
		if (major != other.major) {
			return false;
		}
		if (minor != other.minor) {
			return false;
		}
		if (build >= other.build) {
			return true;
		}
		return false;
	}

	/**
	 * Compares two version identifiers for order using multi-decimal
	 * comparison.
	 * 
	 * @param other the other version identifier
	 * @return <code>true</code> if this version identifier is greater than the
	 *         given version identifier, and <code>false</code> otherwise
	 */
	public boolean isGreaterThan(final Version other) {
		if (other == null) {
			return false;
		}
		if (major > other.major) {
			return true;
		}
		if (major < other.major) {
			return false;
		}
		if (minor > other.minor) {
			return true;
		}
		if (minor < other.minor) {
			return false;
		}
		if (build > other.build) {
			return true;
		}
		if (name > other.name) {
			return true;
		}

		return false;

	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Version)) {
			return false;
		}
		Version o = (Version) obj;
		if (major != o.major || minor != o.minor || build != o.build || name != o.name) {
			return false;
		}

		return true;
	}

	/**
	 * Returns the string representation of this version identifier. The result
	 * satisfies <code>version.equals(new Version(version.toString()))</code>.
	 * 
	 * @return the string representation of this version identifier
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(major);
		sb.append(SEPARATOR).append(minor);
		sb.append(SEPARATOR).append(build);
		sb.append(SEPARATOR).append(name);
		return sb.toString();
	}

	/**
	 * @param obj version to compare this instance with
	 * @return comparison result
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Version obj) {
		if (equals(obj)) {
			return 0;
		}
		if (major != obj.major) {
			return major - obj.major;
		}
		if (minor != obj.minor) {
			return minor - obj.minor;
		}
		if (build != obj.build) {
			return build - obj.build;
		}
		if (name != obj.name) {
			return name - obj.name;
		}
		return 0;
	}

	/**
	 * Converts version to long number.
	 * 
	 * @return Long variable representing this version
	 */
	public long toLong() {
		return ByteBuffer.allocate(8)
			.putShort(major)
			.putShort(minor)
			.putShort(build)
			.putShort(name)
			.getLong(0);
	}

	// Serialization related stuff.

	/**
	 * Write object
	 * 
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(final ObjectOutputStream out) throws IOException {
		out.writeLong(toLong());
	}

	private void readObject(final ObjectInputStream in) throws IOException {
		parseLong(in.readLong());
	}

}
