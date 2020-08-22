open module fr.raksrinana.youtubemp3updater {
	requires java.sql;
	requires fr.raksrinana.utils.base;
	requires fr.raksrinana.utils.http;
	requires fr.raksrinana.utils.config;
	requires org.slf4j;
	requires ch.qos.logback.classic;
	requires info.picocli;
	requires static lombok;
	requires com.fasterxml.jackson.annotation;
	requires com.fasterxml.jackson.databind;
	requires unirest.java;
}