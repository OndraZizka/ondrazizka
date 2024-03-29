package cz.dynawest.util.plugin.cdi;


import cz.dynawest.util.plugin.IPluginLifeCycle;
import cz.dynawest.util.plugin.PluginUtils;
import java.util.*;
import javax.enterprise.inject.Instance;


/**
 *  
 *  @author Ondrej Zizka
 */
public class CdiPluginUtils {
   
   
   /**
    *  Initialization of all available plugins.
    */
   public static <TPlugin extends IPluginLifeCycle, TEx extends Exception> 
           List<TPlugin> initAndStartPlugins( Instance<TPlugin> pluginHookInstances, Object initObject, Class<TEx> exCls ) throws TEx {

      
      // For listing of init errors.
      List<Throwable> exs = new ArrayList<Throwable>();
      List<String> errModules = new ArrayList<String>();
      
      //IModuleHook[] moduleHooks = new IModuleHook[moduleHookInstances];
      List<TPlugin> pluginHooks = new ArrayList();
      for( TPlugin hook : pluginHookInstances.select() ) {
         pluginHooks.add(hook);
      }
      
      // Init
      List<TPlugin> pluginHooks_initOk = new ArrayList();
      for( TPlugin hook : pluginHooks ) {
         try {
            if( null == hook ) continue;
            hook.initModule( initObject );
            pluginHooks_initOk.add(hook);
         } catch( Throwable ex ) {
            exs.add( ex );
            errModules.add( hook.getClass().getName() );
         }
      }
      
      // Start
      for( TPlugin hook : pluginHooks_initOk ) {
         try {
            if( null == hook ) continue;
            hook.startModule();
         } catch( Throwable ex ) {
            exs.add( ex );
            errModules.add( hook.getClass().getName() );
         }
      }
      
      PluginUtils.throwFormattedExceptionIfNeeded( exs, errModules, exCls );
      
      return pluginHooks_initOk;
      
   }// initAndStartPlugins()
   
   
}// class

