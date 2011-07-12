<%@ taglib prefix="s" uri="/struts-tags"%>
<div class="informations">
	<p>
		<SPAN class="message"><s:actionmessage />
		</SPAN>
	</p>
	<p>
		<span class="error"><s:actionerror escape="false" />
		</span>
	</p>
	<p>
		<s:if
			test="exception.class.name == 'fr.certu.chouette.service.commun.ServiceException'">
			<SPAN class="erreur"> <s:text name="%{exception.message}"></s:text>
			</SPAN>
		</s:if>
		<s:elseif
			test="exception.class.name == 'javax.servlet.jsp.JspException'">
			<span class="erreur"> <s:text name="%{'JSP_EXCEPTION'}"></s:text>
			</span>
		</s:elseif>

		<s:elseif test="exception.class.name == 'java.io.IOException'">
			<SPAN class="erreur"> <s:text name="%{'IO_EXCEPTION'}"></s:text>
			</SPAN>
		</s:elseif>
		<s:elseif
			test="exception.class.name == 'org.apache.jasper.JasperException'">
			<SPAN class="erreur"> <s:text name="%{'APACHE_EXCEPTION'}"></s:text>
			</SPAN>
		</s:elseif>
		<s:elseif
			test="exception.class.name == 'java.lang.IllegalArgumentException'">
			<SPAN class="erreur"> <s:text name="%{'ILLEGAL_ARGUMENT'}"></s:text>
			</SPAN>
		</s:elseif>
		<s:elseif
			test="exception.class.name == 'java.lang.NullPointerException'">
			<SPAN class="erreur"> <s:text name="%{'NULLPOINTER'}"></s:text>
			</SPAN>
		</s:elseif>
		<s:elseif test="exception.class.name != ''">
			<SPAN class="erreur"> <s:text name="AUTRE"></s:text> </SPAN>
		</s:elseif>
	</p>
</div>