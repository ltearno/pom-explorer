package fr.lteconsulting.biloutte;

import fr.lteconsulting.superman.Superman;

@Superman
public interface MaBiloutte
{
	int activate();

	String demande( String sujet, int nb, MaBiloutte yesterdays );
}
