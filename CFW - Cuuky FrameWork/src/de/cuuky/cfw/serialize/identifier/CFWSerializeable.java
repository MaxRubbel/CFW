package de.cuuky.cfw.serialize.identifier;

public interface CFWSerializeable {

	/**
	 * To mark classes
	 */

	void onDeserializeEnd();

	void onSerializeStart();

}