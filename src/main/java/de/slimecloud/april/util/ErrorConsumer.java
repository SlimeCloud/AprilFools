package de.slimecloud.april.util;

public interface ErrorConsumer<T> {
	void accept(T arg) throws Exception;
}
