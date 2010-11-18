
package cz.oz.wicket.stack.pages.i18n;


import java.util.*;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.PropertyModel;


/**
 * Dropdown with Locales. From wicket examples.
 */
public class LocaleDropDownChoice extends DropDownChoice<Locale>
{
  
    // We will store the locale to the page's "locale" property.
    private final Page page;
    protected PropertyModel<Locale> propertyModel;
    
  
    /**
     * Construct.
     */
    public LocaleDropDownChoice( String id, List<Locale> locales, Page page )
    {
        super(id, locales, new LocaleChoiceRenderer( page.getLocale() ) );
        this.page = page;
        
        // Set the model that gets the current locale, and that is used for
        // updating the current locale to property 'locale' of FormInput
        this.propertyModel = new PropertyModel<Locale>( this.page, "locale");
        setDefaultModel( this.propertyModel );
    }
    
    //private Locale getCurrentLocale(){ return this.propertyModel.getObject(); }

    /**
     * @see org.apache.wicket.markup.html.form.DropDownChoice#onSelectionChanged(java.lang.Object)
     */
    @Override
    public void onSelectionChanged(Locale newSelection)
    {
        // Note that we don't have to do anything here, as our property model 
        // allready calls FormInput.setLocale when the model is updated.

        // Force re-render by setting the page to render to the bookmarkable instance,
        // so that the page will be rendered from scratch, re-evaluating the input patterns etc.
        //setResponsePage( this.findPage() );  // TranslatedPage.class
        //dashorst: OndraZizka2: wicket will automatically re-render the current page if not instructed to do something else
    }

    /**
     * @see org.apache.wicket.markup.html.form.DropDownChoice#wantOnSelectionChangedNotifications()
     */
    @Override
    protected boolean wantOnSelectionChangedNotifications()
    {
        // we want roundtrips when a the user selects another item
        return true;
    }

    
      
}//class





    /**
     * Choice for a locale.
     */
    final class LocaleChoiceRenderer extends ChoiceRenderer<Locale>
    {
      
        // What input is this renderer for.
        //LocaleDropDownChoice lddch;
        
        private Locale localeToRenderWith;

        public LocaleChoiceRenderer( Locale localeToRenderWith ) {
          this.localeToRenderWith = localeToRenderWith;
        }


        /**
         * @see org.apache.wicket.markup.html.form.IChoiceRenderer#getDisplayValue(Object)
         */
        @Override
        public Object getDisplayValue(Locale locale)
        {
            String display = locale.getDisplayName( this.localeToRenderWith );
            return display;
        }
    }

