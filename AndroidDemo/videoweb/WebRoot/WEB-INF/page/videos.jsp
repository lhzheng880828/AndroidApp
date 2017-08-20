<%@ page language="java" contentType="text/xml; charset=UTF-8" pageEncoding="UTF-8"%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><?xml version="1.0" encoding="UTF-8"?>
<videos><c:forEach items="${videos}" var="video">
	<video id="${video.id}">
		<title>${video.title}</title>
		<timelength>${video.time}</timelength>
	</video></c:forEach>
</videos>