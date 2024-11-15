package qengine.storage;

import fr.boreal.model.logicalElements.api.*;
import fr.boreal.model.logicalElements.impl.SubstitutionImpl;
import org.apache.commons.lang3.NotImplementedException;
import qengine.model.RDFAtom;
import qengine.model.StarQuery;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implémentation d'un HexaStore pour stocker des RDFAtom.
 * Cette classe utilise six index pour optimiser les recherches.
 * Les index sont basés sur les combinaisons (Sujet, Prédicat, Objet), (Sujet, Objet, Prédicat),
 * (Prédicat, Sujet, Objet), (Prédicat, Objet, Sujet), (Objet, Sujet, Prédicat) et (Objet, Prédicat, Sujet).
 */
public class RDFHexaStore implements RDFStorage {
	
	private Map<Term,Integer> dico = new HashMap<>();
  
    //Manao arbre
	private Map<Integer,Map<Integer,List<Integer>>> spo = new HashMap<>();
    
    @Override
    public boolean add(RDFAtom atom) {
    	
    	//préparer structure de donnée dictionaire (hashmap) permettant 
    	//d encoder les element du triplet atom
    	//mettre a jour le dico avec les valeur du triplet
    	//ajouter triplet encoder dans l'index
    	
    	if (atom == null) return false;
    	
    	Term s = atom.getTripleSubject();
        Term p = atom.getTriplePredicate();
        Term o = atom.getTripleObject();
    	
        //
        boolean added = false;
        if (!dico.containsKey(s)) {
            dico.put(s, dico.size());
            added = true;
        }
        if (!dico.containsKey(p)) {
            dico.put(p, dico.size());
            added = true;
        }
        if (!dico.containsKey(o)) {
            dico.put(o, dico.size());
            added = true;
        }
        
        
        //raha tsy ao anaty dico
        if (added) {
            
        	var indexS=dico.get(s); //
        	var indexP=dico.get(p);
        	var indexO=dico.get(o);
        	
        	//SPO
        	if (spo.containsKey(indexS)) {
        		var po=spo.get(indexS);
        		if (po.containsKey(indexP)) {
        			var oo= po.get(indexP);
        			if (!oo.contains(indexO)) {
        				oo.add(indexO);
        			}
        		}
        	}else {
        		var lo=List.of(indexO);
        		var mp= new HashMap<Integer,List<Integer>> ();
        		mp.put(indexP,lo);
        		spo.put(indexS, mp);
        	}
        	
        	
        }
        
        return added;
    }

    @Override
    public long size() {
    	throw new NotImplementedException();
    }

    @Override
    public Iterator<Substitution> match(RDFAtom atom) {
    	throw new NotImplementedException();
    }
    
    

    @Override
    public Iterator<Substitution> match(StarQuery q) {
    	throw new NotImplementedException();
    }

    @Override
    public Collection<Atom> getAtoms() {
    	throw new NotImplementedException();
    }
}
