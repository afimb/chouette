package fr.certu.chouette.struts.outil.displaytag;

import java.util.Iterator;
import java.util.Locale;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.views.jsp.TagUtils;
import org.displaytag.Messages;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.ognl.OgnlValueStack;
import com.opensymphony.xwork2.util.ValueStack;
import org.displaytag.localization.I18nResourceProvider;
import org.displaytag.localization.LocaleResolver;

/**
 * Struts2/Webwork2 implementation of a resource provider and locale
resolver.<br/>
 * Tested with diplaytag 1.1 and struts2 2.0.1<br/>
 * # Set this values in displaytag.properties #<br/>
 * <code>
 * locale.resolver = org.displaytag.localization.I18nStruts2Adapter<br/>
 * locale.provider = org.displaytag.localization.I18nStruts2Adapter<br/>
 * </code>
 * Date: 08-mar-2007
 * Time: 13:11:31
 * @author Carles Gasques Baró
 * @version 1.0
 */
public class I18nStruts2Adapter implements LocaleResolver, I18nResourceProvider
{

  public static final String UNDEFINED_KEY = "???";
  private static Log log = LogFactory.getLog(I18nStruts2Adapter.class);

  @Override
  public Locale resolveLocale(HttpServletRequest request)
  {
    Locale result = null;
    ValueStack stack = ActionContext.getContext().getValueStack();

    Iterator iterator = stack.getRoot().iterator();
    while (iterator.hasNext())
    {
      Object o = iterator.next();

      if (o instanceof LocaleProvider)
      {
        LocaleProvider lp = (LocaleProvider) o;
        result = lp.getLocale();

        break;
      }
    }

    if (result == null)
    {
      log.debug("Missing LocalProvider actions, init locale to default");
      result = Locale.getDefault();
    }

    return result;
  }

  @Override
  public String getResource(String resourceKey, String defaultValue, Tag tag, PageContext pageContext)
  {
    String key = (resourceKey != null) ? resourceKey : defaultValue;
    String message = null;
    OgnlValueStack stack =
            (OgnlValueStack) TagUtils.getStack(pageContext);
    Iterator iterator = stack.getRoot().iterator();

    while (iterator.hasNext())
    {
      Object o = iterator.next();

      if (o instanceof TextProvider)
      {
        TextProvider tp = (TextProvider) o;
        message = tp.getText(key, defaultValue,
                Collections.EMPTY_LIST, stack);

        break;
      }
    }

    if (message == null && resourceKey != null)
    {
      log.debug(Messages.getString("Localization.missingkey",
              resourceKey));
      message = UNDEFINED_KEY + resourceKey + UNDEFINED_KEY;
    }

    return message;
  }
}
