
package qengine.storage;

//public class Tsiory {
	
	//package qengine.storage;

	//import fr.boreal.model.logicalElements.api.*;
	//import fr.boreal.model.logicalElements.impl.SubstitutionImpl;
	//import org.apache.commons.lang3.NotImplementedException;
//import qengine.model.RDFAtom;
//import qengine.model.StarQuery;

//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;

	/**
	 * Implémentation d'un HexaStore pour stocker des RDFAtom.
	 * Cette classe utilise six index pour optimiser les recherches.
	 * Les index sont basés sur les combinaisons (Sujet, Prédicat, Objet), (Sujet, Objet, Prédicat),
	 * (Prédicat, Sujet, Objet), (Prédicat, Objet, Sujet), (Objet, Sujet, Prédicat) et (Objet, Prédicat, Sujet).
	 
	

	public class RDFHexaStore implements RDFStorage {
		private Map<Term,Integer> dico = new HashMap<>();
	    private List<RDFAtom> atoms = new ArrayList<>();
	    

	    // Définition des six index pour les permutations des triplets
	    private Map<List<Integer>, RDFAtom> sop = new HashMap<>();
	    private Map<List<Integer>, RDFAtom> osp = new HashMap<>();
	    private Map<List<Integer>, RDFAtom> pos = new HashMap<>();
	    private Map<List<Integer>, RDFAtom> pso = new HashMap<>();
	    private Map<List<Integer>, RDFAtom> ops = new HashMap<>();
	    private Map<List<Integer>, RDFAtom> spo = new HashMap<>();
	
	    private Map<Integer,Map<Integer,List<Integer>>> spo= new HashMap<>();
	    
	    @Override
	    public boolean add(RDFAtom atom) {
	    	
	    	//préparer structure de donnée dictionaire (hashmap) permettant 
	    	//d encoder les element du triplet atom
	    	//mettre a jour le dico avec les valeur du triplet
	    	//ajouter triplet encoder dans l'index
	    	Term s = atom.getTripleSubject();
	        Term p = atom.getTriplePredicate();
	        Term o = atom.getTripleObject();
	    	
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
	        
	        if (added) {
	            
	        	var es=dico.get(s);
	        	var ep=dico.get(p);
	        	var eo=dico.get(o);
	        	
	        	if (spo.containsKey(es)) {
	        		var po=spo.get(es);
	        		if (po.containsKey(ep)) {
	        			var oo= po.get(ep);
	        			if (!oo.contains(eo)) {
	        				oo.add(eo);
	        			}
	        		}
	        	}else {
	        		var lo=List.of(eo);
	        		var mp= new HashMap<Integer,List<Integer>> ();
	        		mp.put(ep,lo);
	        		spo.put(es, mp);
	        	}
	        	
	        	
	            //List<Integer> sopKey = Arrays.asList(dico.get(s), dico.get(p), dico.get(o));
	            List<Integer> ospKey = Arrays.asList(dico.get(o), dico.get(s), dico.get(p));
	            List<Integer> posKey = Arrays.asList(dico.get(p), dico.get(o), dico.get(s));
	            List<Integer> psoKey = Arrays.asList(dico.get(p), dico.get(s), dico.get(o));
	            List<Integer> opsKey = Arrays.asList(dico.get(o), dico.get(p), dico.get(s));
	            List<Integer> spoKey = Arrays.asList(dico.get(s), dico.get(o), dico.get(p));
	            
	            
	            sop.put(sopKey, atom);
	            osp.put(ospKey, atom);
	            pos.put(posKey, atom);
	            pso.put(psoKey, atom);
	            ops.put(opsKey, atom);
	            spo.put(spoKey, atom);
	            
	            atoms.add(atom);
	        }
	        
	        return added;
	    }

	    @Override
	    public long size() {
	        return atoms.size();
	    }

	    @Override
	    public Iterator<Substitution> match(RDFAtom atom) {
	    	List<Substitution> substitutions = new ArrayList<>();
	        
	        // Créer les clés de recherche pour chaque permutation
	        List<Integer> sopKey = Arrays.asList(dico.get(atom.getTripleSubject()), dico.get(atom.getTriplePredicate()), dico.get(atom.getTripleObject()));
	        List<Integer> ospKey = Arrays.asList(dico.get(atom.getTripleObject()), dico.get(atom.getTripleSubject()), dico.get(atom.getTriplePredicate()));
	        List<Integer> posKey = Arrays.asList(dico.get(atom.getTriplePredicate()), dico.get(atom.getTripleObject()), dico.get(atom.getTripleSubject()));
	        List<Integer> psoKey = Arrays.asList(dico.get(atom.getTriplePredicate()), dico.get(atom.getTripleSubject()), dico.get(atom.getTripleObject()));
	        List<Integer> opsKey = Arrays.asList(dico.get(atom.getTripleObject()), dico.get(atom.getTriplePredicate()), dico.get(atom.getTripleSubject()));
	        List<Integer> spoKey = Arrays.asList(dico.get(atom.getTripleSubject()), dico.get(atom.getTripleObject()), dico.get(atom.getTriplePredicate()));

	        // Recherche dans chaque permutation d'index
	        if (sop.containsKey(sopKey)) substitutions.add(new SubstitutionImpl());
	        if (osp.containsKey(ospKey)) substitutions.add(new SubstitutionImpl());
	        if (pos.containsKey(posKey)) substitutions.add(new SubstitutionImpl());
	        if (pso.containsKey(psoKey)) substitutions.add(new SubstitutionImpl());
	        if (ops.containsKey(opsKey)) substitutions.add(new SubstitutionImpl());
	        if (spo.containsKey(spoKey)) substitutions.add(new SubstitutionImpl());
	        
	        return substitutions.iterator();
	    }
	    
	    

	    @Override
	    public Iterator<Substitution> match(StarQuery q) {
	    	List<Substitution> substitutions = new ArrayList<>();

	        
	        for (RDFAtom queryAtom : q.getRdfAtoms()) {
	            for (RDFAtom storedAtom : atoms) {
	                boolean matches = true;
	                for (int i = 0; i < queryAtom.getTerms().length; i++) {
	                    Term queryTerm = queryAtom.getTerms()[i];
	                    Term storedTerm = storedAtom.getTerms()[i];

	                    // Si c'est une variable, on l'ignore
	                    if (queryTerm instanceof Variable) {
	                        continue;
	                    }

	                    // Comparaison des termes
	                    if (!storedTerm.equals(queryTerm)) {
	                        matches = false;
	                        break;
	                    }
	                }

	                // Si tous les atomes correspondent, on ajoute une substitution
	                if (matches) {
	                    substitutions.add(new SubstitutionImpl());
	                }
	            }
	        }
	        
	        return substitutions.iterator();
	    }

	    @Override
	    public Collection<Atom> getAtoms() {
	        return new ArrayList<>(atoms);

	    }
	}


}

*/
