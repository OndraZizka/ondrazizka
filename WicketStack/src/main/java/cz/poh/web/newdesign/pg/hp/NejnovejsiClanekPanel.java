
package cz.poh.web.newdesign.pg.hp;

import cz.poh.web.newdesign.model.Clanek;
import cz.poh.web.newdesign.pn.ClanekPanel;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author ondra
 */
public final class NejnovejsiClanekPanel extends Panel
{

    public NejnovejsiClanekPanel( String id ) {
        super(id);

				add( new ClanekPanel("clanek", this.getNejnovejsiClanek() )).setRenderBodyOnly(true);
    }


		private Clanek getNejnovejsiClanek(){

				return new Clanek("Novela insolvenčního zákona zvýší šanci zaměstnanců",
						 "<p>"
						+"\n<strong>Insolvenční rejstřík.info</strong> je služba, která za vás sleduje Insolvenční rejstřík.<br />"
						+"\nKaždé čtyři minuty aktualizujeme naši databázi dlužníků, úpadců a firem v konkurzu podle"
						+"\n<a href=\"http://isir.justice.cz/\">webové služby Insolvenčního rejstříku (ISIR)</a>"
						+"\na 24 hodin denně sledujeme, zda se mezi novými událostmi objevili právě vaši dlužníci!"
						+"\n</p><p>"
						+"\n<strong>Insolvenční rejstřík.info</strong> je nejen nejpřehlednější zdroj informací"
						+"\no insolvenčních řízeních, ale také bohatá studna praktických právních informací."
						+"\nPoradíme vám, jak sledovat Insolvenční rejstřík, aby vám neunikla jediná pohledávka,"
						+"\nco dělat, pokud se v insolvenčním rejstříku oběví právě váš dlužník,"
						+"\na jak získat ze svojí pohledávky maximální možnou část."
						+"\n</p>"
				);
		}

}// class NejnovejsiClanekPanel
