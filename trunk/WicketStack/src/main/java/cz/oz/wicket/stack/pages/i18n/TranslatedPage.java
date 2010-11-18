
package cz.oz.wicket.stack.pages.i18n;


import cz.oz.wicket.stack.pages.BaseLayoutPage;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.PageParameters;


/**
 *
 * @author Ondrej Zizka
 */
public class TranslatedPage extends BaseLayoutPage
{
  
  
    /** Relevant locales wrapped in a list. */
    public static final List<Locale> LOCALES = Arrays.asList(new Locale[] {
            Locale.ENGLISH,
            new Locale("cs", "CZ")  });


    public TranslatedPage( PageParameters parameters ) {
      super( parameters );

      add( new LocaleDropDownChoice( "locale", LOCALES,  this ) );

    }

    /**
     * Wicket's Component class has getLocale(), but not setLocale(), as locale
     * is handled automatically based on HTTP headers.
     * Hereby we fix that since we want to set it manually.
     * Then we use it in LocaleDropDownChoice in a PropertyModel(page, "locale").
     */
    public void setLocale( Locale locale ) {
        if( locale == null ) return;
        getSession().setLocale(locale);
    }  


}// class TranslatedPage
