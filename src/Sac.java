import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class Sac {
    int nbVar;
    int nbContrainteCapacite;
    int nbContrainteDemande;
    ArrayList<Integer> contrainteCapacite = new ArrayList<Integer>(); // <= Membre de droite seulement
    ArrayList<Integer> contrainteDemande = new ArrayList<Integer>(); // >= Membre de droite seulement

    ArrayList<Objet> objets = new ArrayList<Objet>();
    ArrayList<Objet> objetsSauvegarde = new ArrayList<Objet>();//sauvegarde des adresses memoire des objets pour pouvoir les retrouver a la fin de l'algo glouton

    public Sac() {
        super();
    }
    
    //================================== gestion des fichiers ==========================================================================

    /**
     * Ecrit dans un fichier le resultat de l'algorithme
     * @param nomFichierSortie nom du fichier de sortie
     * @param nomFichierEntree nom du fichier qui a servi a la generation du sac
     */
    public void ecrireFichier(String nomFichierSortie, String nomFichierEntree) {
        File f = new File(nomFichierSortie);
        int sommeValeur = 0;

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));

            pw.println(nomFichierEntree);
            pw.println(nbVar);

            for (Objet o : objetsSauvegarde) {
                pw.print((o.variable ? 1 : 0) + " ");
                sommeValeur += o.valeur;
            }
            pw.println("");
            pw.print(sommeValeur);
            pw.close();
        } catch (IOException exception) {
            System.out.println("Erreur lors de la lecture : " + exception.getMessage());
        }

    }
    
    /**
     * A partir d'un fichier, creer un sac
     * @param nomFichier nom du fichier servant a la creation du sac
     */
    public void lectureFichier(String nomFichier) {
        try {

            File file = new File(nomFichier);

            Scanner input = new Scanner(file);

            nbVar = input.nextInt();
            nbContrainteCapacite = input.nextInt();
            nbContrainteDemande = input.nextInt();

            for (int i = 0; i < nbVar; i++) {
                objets.add(new Objet(input.nextInt()));
            }

            for (int i = 0; i < nbContrainteCapacite; i++) {
                contrainteCapacite.add(input.nextInt());
            }

            for (int i = 0; i < nbContrainteDemande; i++) {
                contrainteDemande.add(input.nextInt());
            }

            // recuperation des contraintes de capacitï¿½
            for (int i = 0; i < nbContrainteCapacite; i++) {
                for (int j = 0; j < nbVar; j++) {
                    objets.get(j).getContrainteCapacite().add(input.nextInt());
                }
            }

            // recuperation des contraintes de demande
            for (int i = 0; i < nbContrainteDemande; i++) {
                for (int j = 0; j < nbVar; j++) {
                    objets.get(j).getContrainteDemande().add(input.nextInt());
                }
            }

            input.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // sauvegarde des objets car l'algorithme glouton en supprime des objets
        for (Objet o : objets) {
            objetsSauvegarde.add(o);
        }
    }
    //==================================  fin gestion des fichiers ==========================================================================


    //====================================================================debut glouton====================================================================
    /**
     * Algorithme glouton servant a la resolution du probleme
     */
    public void algoGlouton() {
        ArrayList<Integer> conCap;
        ArrayList<Integer> conDem;

        // tri de la liste
        Collections.sort(objets, new Comparator<Objet>() {
            public int compare(Objet o1, Objet o2) {
                return o2.getValeur() - o1.getValeur();
            }
        });

        int indiceDemandeMoinsSatifaite = -2;
        // selections des objets
        while (!objets.isEmpty() && indiceDemandeMoinsSatifaite != -1) {
            conCap = (ArrayList<Integer>) contrainteCapacite.clone();
            conDem = (ArrayList<Integer>) contrainteDemande.clone();
            for (Objet o : objets) {
                if (objetPossible(o, conCap, conDem)) {
                    ajoutObjet(o, conCap, conDem);
                }
            }
            indiceDemandeMoinsSatifaite = demandeSatifaite(conDem);
            if (indiceDemandeMoinsSatifaite != -1) {// si les demandes des  objets pris n'est pas a  moins de 0
                supprimerObjetDemandeFaible(indiceDemandeMoinsSatifaite);// retirer  l'objet  avec  la  demande  la  plus  faible  dans  le  objets  ajouter  dans  le  sac
                objets.forEach((objet) -> objet.variable = false);// remettre  les  objets  hors du   sac
            }
        }
    }


    /**
     * Supprime l'objet le moins satifait 
     * Si les contraintes de demande ne sont pas satifaites on retire l'objet qui a la demande la plus faible 
     * (si 2 objets ont la meme demande c'est celui avec la valeur la plus faible qui est supprimé)
     * @param indiceDemandeNonSatifaite l'indice du membre droit le moins satifait
     */
    private void supprimerObjetDemandeFaible(int indiceDemandeNonSatifaite) {
        int demandePlusFaible = Integer.MAX_VALUE;
        int indiceDemandePlusFaible = 0;
        for (int i = 0; i < objets.size(); i++) {
            if (demandePlusFaible >= objets.get(i).contrainteDemande.get(indiceDemandeNonSatifaite)
                    && objets.get(i).variable) {//si le tampon est plus elevé que la demande de l'objet 
                demandePlusFaible = objets.get(i).contrainteDemande.get(indiceDemandeNonSatifaite);
                indiceDemandePlusFaible = i;
            }
        }
        objets.remove(indiceDemandePlusFaible);
    }

    /**
     * Test si une demande et renvoie si une demande est nonsatifaite
     * Trouve la demande la moins satifaite des demandes non satifaites (demande => 0) et renvoie son indice si il en existe
     * @param conDem les membres droits des contraintes de demmande
     * @return -1 si satifaite ou l'indice de la demande la moins satifaite
     */
    private int demandeSatifaite(ArrayList<Integer> conDem) {
        int demMoinsSatifaite = 0;// la demande la moins satifaite
        int indice = -1;// renvoie -1 si la demande est satifaite
        for (int i = 0; i < conDem.size(); i++) {
            if (demMoinsSatifaite < conDem.get(i)) {// si c'est la demande la  moins satifaite on  remplace
                demMoinsSatifaite = conDem.get(i);
                indice = i;
            }
        }
        return indice;
    }

    /**
     * renvoie si l'objet peut etre ajouter au sac
     * @param o Objet a tester
     * @param conCap les membres droits des contraintes de capacite
     * @param conDem les membres droits des contraintes de demmande
     * @return
     */
    private boolean objetPossible(Objet o, ArrayList<Integer> conCap, ArrayList<Integer> conDem) {
        boolean objetPossible = true;// permet de savoir si l'objet est ajoutable ou non dans le sac
        for (int i = 0; i < o.contrainteCapacite.size(); i++) {
            objetPossible &= conCap.get(i) - o.contrainteCapacite.get(i) >= 0;// verifie si toutes les contraintes de capacite sont respectées
        }

        return objetPossible;
    }

    /**
     * Ajoute l'objet au sac et met a jour les menbres droit du sac
     * @param o Objet a ajouter dans le sac
     * @param conCap les membres droits des contraintes de capacite
     * @param conDem les membres droits des contraintes de demmande
     */
    private void ajoutObjet(Objet o, ArrayList<Integer> conCap, ArrayList<Integer> conDem) {
        o.variable = true;
        for (int i = 0; i < o.contrainteCapacite.size(); i++) {
            conCap.set(i, conCap.get(i) - o.contrainteCapacite.get(i));// on reduit la capacite du sac (membre droit des contraintes)
        }
        for (int i = 0; i < o.contrainteDemande.size(); i++) {
            conDem.set(i, conDem.get(i) - o.contrainteDemande.get(i));// on reduit la demande du sac pour voir si a la fin toutes les demande sont inferieures a zero (membre droit des contraintes)
        }
    }

    
    //==================================fin algo glouton============================================================================================================
    
    //==============================getter setter ===================================================================================================================
    public int getNbVar() {
        return nbVar;
    }

    public void setNbVar(int nbVar) {
        this.nbVar = nbVar;
    }

    public int getNbContrainteCapacite() {
        return nbContrainteCapacite;
    }

    public void setNbContrainteCapacite(int nbContrainteCapacite) {
        this.nbContrainteCapacite = nbContrainteCapacite;
    }

    public int getNbContrainteDemande() {
        return nbContrainteDemande;
    }

    public void setNbContrainteDemande(int nbContrainteDemande) {
        this.nbContrainteDemande = nbContrainteDemande;
    }

    public ArrayList<Integer> getContrainteCapacite() {
        return contrainteCapacite;
    }

    public void setContrainteCapacite(ArrayList<Integer> contrainteCapacite) {
        this.contrainteCapacite = contrainteCapacite;
    }

    public ArrayList<Integer> getContrainteDemande() {
        return contrainteDemande;
    }

    public void setContrainteDemande(ArrayList<Integer> contrainteDemande) {
        this.contrainteDemande = contrainteDemande;
    }

    public ArrayList<Objet> getObjets() {
        return objets;
    }

    public void setObjets(ArrayList<Objet> objets) {
        this.objets = objets;
    }

    @Override
    public String toString() {
        return "nbVar : " + nbVar + "\n" + "nbContrainteCapacite : " + nbContrainteCapacite + "\n"
                + "nbContrainteCapacite : " + nbContrainteDemande + "\n" + "objets : " + objets;
    }

    //======================================================fin getter setter=================================================================================

    //===================================================test===============================================================================================
    /**
     * Fonction de test permettant de compter les differents membre droit des contraintes en fonction des objet ajouté dans le sac
     */
    public void sommeSac() {//permet de tester si les valeurs sont coherentes
        ArrayList<Integer> sommeCapacite = (ArrayList<Integer>) contrainteCapacite.clone();
        for (int i = 0; i < sommeCapacite.size(); i++) {
            sommeCapacite.set(i, 0);
        }
        ArrayList<Integer> sommeDemande = (ArrayList<Integer>) contrainteDemande.clone();
        for (int i = 0; i < sommeDemande.size(); i++) {
            sommeDemande.set(i, 0);
        }
        System.out.println("\n somme des capacites" + sommeCapacite);
        System.out.println("\n somme des demandes" + sommeDemande);
        for (int i = 0; i < contrainteCapacite.size(); i++) {
            for (Objet o : objets) {
                if (o.variable)
                    sommeCapacite.set(i, sommeCapacite.get(i) + o.contrainteCapacite.get(i));

            }
        }
        for (int i = 0; i < contrainteDemande.size(); i++) {
            for (Objet o : objets) {
                if (o.variable)
                    sommeDemande.set(i, sommeDemande.get(i) + o.contrainteDemande.get(i));
            }
        }
        System.out.println("\n somme des capacites" + sommeCapacite);
        System.out.println("\n somme des demandes" + sommeDemande);
    }
}
