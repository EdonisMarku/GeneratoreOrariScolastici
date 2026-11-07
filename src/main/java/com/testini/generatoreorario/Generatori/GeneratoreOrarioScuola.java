package com.testini.generatoreorario.Generatori;

import com.testini.generatoreorario.ClassiBasi.*;
import java.util.*;

public class GeneratoreOrarioScuola {

    private static final int MAX_TENTATIVI = 200;
    private static final int MAX_SCAMBI = 20000;

    private final Scuola scuola;
    private final int giorni = GiorniSettimana.GIORNI_SCOLASTICI;
    private final int ore = GiorniSettimana.ORE_AL_GIORNO;
    private final int TUTTI = (1 << (giorni * ore)) - 1;
    private final Random rand = new Random();

    private Map<Docente, Integer> bitDoc;
    private Map<Docente, Integer> bitLiberi;
    private Map<Classe, Integer> bitCla;
    private Map<Classe, Lezione[][][]> orari;

    private record Gruppo(List<Docente> doc, Classe cl, Materia mat, int ore) {}
    private record Req(List<Docente> doc, Classe cl, Materia mat) {}

    public GeneratoreOrarioScuola(Scuola scuola) {
        this.scuola = scuola;
    }

    private int bit(int o, int g) {
        return 1 << (g * ore + o);
    }

    private int liberi(Req r) {
        int occ = bitCla.getOrDefault(r.cl, 0);
        for (Docente d: r.doc) {
            occ |= bitDoc.getOrDefault(d, 0) | bitLiberi.getOrDefault(d, 0);
        }
        return ~occ & TUTTI;
    }

    private int[] casuale(int bits) {
        int n = rand.nextInt(Integer.bitCount(bits));
        for (int k = 0; k < n; k++) {
            bits &= bits - 1;
        }
        int p = Integer.numberOfTrailingZeros(bits);
        return new int[]{p % ore, p / ore};
    }

    private List<int[]> tutti(int bits) {
        List<int[]> l = new ArrayList<>();
        while (bits != 0) {
            int p = Integer.numberOfTrailingZeros(bits);
            l.add(new int[]{p % ore, p / ore});
            bits &= bits - 1;
        }
        return l;
    }

    private void assegna(Req r, int o, int g) {
        int b = bit(o, g);
        Lezione[] lez = new Lezione[r.doc.size()];
        for (int i = 0; i < r.doc.size(); i++) {
            Docente d = r.doc.get(i);
            lez[i] = new Lezione(d, r.mat, r.cl);
            bitDoc.merge(d, b, (a, v) -> a | v);
        }
        orari.get(r.cl)[o][g] = lez;
        bitCla.merge(r.cl, b, (a, v) -> a | v);
    }

    private Req rimuovi(Classe cl, int o, int g) {
        Lezione[] lez = orari.get(cl)[o][g];
        if (lez == null || lez.length == 0) {
            return null;
        }
        int b = bit(o, g);
        List<Docente> doc = new ArrayList<>();
        for (Lezione l: lez) {
            Docente d = l.getDocente();
            doc.add(d);
            bitDoc.put(d, bitDoc.get(d) & ~b);
        }
        orari.get(cl)[o][g] = null;
        bitCla.put(cl, bitCla.get(cl) & ~b);
        return new Req(doc, cl, lez[0].getMateria());
    }

    public boolean generaOrario() {
        List<Gruppo> gruppi = identificaGruppi();
        int obiettivo = gruppi.stream().mapToInt(Gruppo::ore).sum();
        for (Docente d: scuola.getTuttiIDocenti()) {
            if (d.getOreTotali() > giorni * ore) {
                obiettivo -= (d.getOreTotali() - giorni * ore);
            }
        }

        int best = 0;
        Map<Classe, Lezione[][][]> bestOrario = null;

        for (int t = 0; t < MAX_TENTATIVI; t++) {
            int p = tentativo(gruppi);
            if (p >= obiettivo) {
                copiaFinale();
                return true;
            }
            if (p > best) {
                best = p;
                bestOrario = copiaOrari();
            }
        }

        if (bestOrario != null) {
            orari = bestOrario;
            copiaFinale();
        }
        return best >= obiettivo;
    }

    private int tentativo(List<Gruppo> gruppi) {
        bitDoc = new HashMap<>();
        for (Docente d: scuola.getTuttiIDocenti()) {
            bitDoc.put(d, 0);
        }

        bitCla = new HashMap<>();
        orari = new HashMap<>();
        for (Classe c: scuola.getTutteLeClassi()) {
            bitCla.put(c, 0);
            orari.put(c, new Lezione[ore][giorni][]);
        }

        bitLiberi = new HashMap<>();
        for (Docente d: scuola.getTuttiIDocenti()) {
            int m = 0;
            for (GiorniSettimana gl: d.getGiorniLiberi()) {
                int gi = gl.ordinal();
                if (gi < giorni) {
                    for (int o = 0; o < ore; o++) {
                        m |= bit(o, gi);
                    }
                }
            }
            bitLiberi.put(d, m);
        }

        List<Req> richieste = new ArrayList<>();
        for (Gruppo g: gruppi) {
            for (int i = 0; i < g.ore; i++) {
                richieste.add(new Req(g.doc, g.cl, g.mat));
            }
        }
        Collections.shuffle(richieste, rand);
        richieste.sort(Comparator.comparingInt(r -> Integer.bitCount(liberi(r))));

        int piazzati = 0;
        List<Req> fallite = new ArrayList<>();
        for (Req r: richieste) {
            int l = liberi(r);
            if (l != 0) {
                int[] s = casuale(l);
                assegna(r, s[0], s[1]);
                piazzati++;
            } else {
                fallite.add(r);
            }
        }

        for (int iter = 0; iter < MAX_SCAMBI && !fallite.isEmpty(); iter++) {
            Req r = fallite.get(rand.nextInt(fallite.size()));
            int docViet = 0;
            for (Docente d: r.doc) {
                docViet |= bitDoc.getOrDefault(d, 0) | bitLiberi.getOrDefault(d, 0);
            }
            int cand = bitCla.getOrDefault(r.cl, 0) & ~docViet & TUTTI;
            List<int[]> slots = tutti(cand);
            Collections.shuffle(slots, rand);

            boolean ok = false;
            for (int[] s: slots) {
                Req spost = rimuovi(r.cl, s[0], s[1]);
                int lib = liberi(spost);
                if (lib != 0) {
                    int[] ns = casuale(lib);
                    assegna(spost, ns[0], ns[1]);
                    assegna(r, s[0], s[1]);
                    fallite.remove(r);
                    piazzati++;
                    ok = true;
                    break;
                } else {
                    assegna(spost, s[0], s[1]);
                }
            }

            if (!ok) {
                for (Docente d: r.doc) {
                    if (ok) {
                        break;
                    }
                    int conf = bitDoc.get(d) & ~bitCla.getOrDefault(r.cl, 0) & TUTTI;
                    for (int[] cs: tutti(conf)) {
                        Lezione lez = trovaLezioneDocente(d, cs[0], cs[1]);
                        if (lez == null) {
                            continue;
                        }
                        Req spost = rimuovi(lez.getClasse(), cs[0], cs[1]);
                        int lib = liberi(spost);
                        if (lib == 0) {
                            assegna(spost, cs[0], cs[1]);
                            continue;
                        }
                        int[] ns = casuale(lib);
                        assegna(spost, ns[0], ns[1]);
                        int lr = liberi(r);
                        if (lr != 0) {
                            int[] sr = casuale(lr);
                            assegna(r, sr[0], sr[1]);
                            fallite.remove(r);
                            piazzati++;
                            ok = true;
                            break;
                        } else {
                            rimuovi(lez.getClasse(), ns[0], ns[1]);
                            assegna(spost, cs[0], cs[1]);
                        }
                    }
                }
            }
        }
        return piazzati;
    }

    private List<Gruppo> identificaGruppi() {
        Map<String, List<Assegnazione>> mappa = new LinkedHashMap<>();
        for (Assegnazione a: scuola.getAssegnazioni()) {
            String chiave = a.getClasse().getNomeCompleto() + "|" + a.getMateria().getNomeMateria();
            mappa.computeIfAbsent(chiave, k -> new ArrayList<>()).add(a);
        }

        List<Gruppo> gruppi = new ArrayList<>();
        for (List<Assegnazione> lista: mappa.values()) {
            if (lista.size() == 1) {
                Assegnazione a = lista.getFirst();
                gruppi.add(new Gruppo(List.of(a.getDocente()), a.getClasse(), a.getMateria(), a.getOreSettimanali()));
                continue;
            }
            int max = lista.stream().mapToInt(Assegnazione::getOreSettimanali).max().orElse(0);
            int min = lista.stream().mapToInt(Assegnazione::getOreSettimanali).min().orElse(0);
            List<Docente> doc = lista.stream().map(Assegnazione::getDocente).toList();
            Assegnazione p = lista.getFirst();
            if (min == max) {
                gruppi.add(new Gruppo(doc, p.getClasse(), p.getMateria(), max));
            } else {
                gruppi.add(new Gruppo(doc, p.getClasse(), p.getMateria(), min));
                for (Assegnazione a : lista) {
                    if (a.getOreSettimanali() > min) {
                        gruppi.add(new Gruppo(List.of(a.getDocente()), a.getClasse(), a.getMateria(), a.getOreSettimanali() - min));
                    }
                }
            }
        }
        return gruppi;
    }

    private Lezione trovaLezioneDocente(Docente doc, int o, int g) {
        for (Classe c: scuola.getTutteLeClassi()) {
            Lezione[][][] or = orari.get(c);
            if (or != null && or[o][g] != null) {
                for (Lezione l : or[o][g]) {
                    if (l.getDocente().equals(doc)) {
                        return l;
                    }
                }
            }
        }
        return null;
    }

    private Map<Classe, Lezione[][][]> copiaOrari() {
        Map<Classe, Lezione[][][]> copia = new HashMap<>();
        for (Classe c: scuola.getTutteLeClassi()) {
            Lezione[][][] orig = orari.get(c);
            Lezione[][][] cp = new Lezione[ore][giorni][];
            for (int o = 0; o < ore; o++) {
                for (int g = 0; g < giorni; g++) {
                    if (orig[o][g] != null) {
                        cp[o][g] = orig[o][g].clone();
                    }
                }
            }
            copia.put(c, cp);
        }
        return copia;
    }

    private void copiaFinale() {
        for (Classe c: scuola.getTutteLeClassi()) {
            Lezione[][] tab = new Lezione[ore][giorni];
            Lezione[][][] or = orari.get(c);
            for (int o = 0; o < ore; o++) {
                for (int g = 0; g < giorni; g++) {
                    if (or[o][g] != null && or[o][g].length > 0) {
                        List<Docente> dc = Arrays.stream(or[o][g]).map(Lezione::getDocente).toList();
                        tab[o][g] = new Lezione(dc, or[o][g][0].getMateria(), c);
                    }
                }
            }
            c.setTabellaLezioni(tab);
        }
    }

    public Lezione[][] estraiOrarioDocente(Docente docente) {
        Lezione[][] od = new Lezione[ore][giorni];
        for (Classe c: scuola.getTutteLeClassi()) {
            Lezione[][][] or = orari.get(c);
            if (or == null) {
                continue;
            }
            for (int o = 0; o < ore; o++) {
                for (int g = 0; g < giorni; g++) {
                    if (or[o][g] != null) {
                        for (Lezione l: or[o][g]) {
                            if (l.getDocente().equals(docente)) {
                                od[o][g] = l;
                            }
                        }
                    }
                }
            }
        }
        return od;
    }

    public List<String> verificaConflitti() {
        List<String> err = new ArrayList<>();
        for (Docente d: scuola.getTuttiIDocenti()) {
            for (int o = 0; o < ore; o++) {
                for (int g = 0; g < giorni; g++) {
                    List<Classe> cl = new ArrayList<>();
                    for (Classe c: scuola.getTutteLeClassi()) {
                        Lezione[][][] or = orari.get(c);
                        if (or != null && or[o][g] != null) {
                            for (Lezione l: or[o][g]) {
                                if (l.getDocente().equals(d)) {
                                    cl.add(c);
                                }
                            }
                        }
                    }
                    if (cl.size() > 1) {
                        err.add("CONFLITTO: " + d.getNomeCognome() + " in più classi il " + GiorniSettimana.values()[g].getNome() + " alla " + (o + 1) + "° ora: " + cl);
                    }
                }
            }
        }
        return err;
    }

    public static void stampaOrarioClasse(Classe classe) {
        System.out.println("\n===== ORARIO CLASSE " + classe.getNomeCompleto() + " =====");
        Lezione[][] or = classe.getTabellaLezioni();
        if (or == null) {
            System.out.println("  (nessun orario generato)");
            return;
        }

        System.out.printf("%-6s", "Ora");
        for (int g = 0; g < GiorniSettimana.GIORNI_SCOLASTICI; g++) {
            System.out.printf("| %-25s", GiorniSettimana.values()[g].getNome());
        }
        System.out.println();
        System.out.println("-".repeat(6 + (28 * GiorniSettimana.GIORNI_SCOLASTICI)));

        for (int o = 0; o < GiorniSettimana.ORE_AL_GIORNO; o++) {
            System.out.printf("%-6s", (o + 1) + "°");
            for (int g = 0; g < GiorniSettimana.GIORNI_SCOLASTICI; g++) {
                Lezione l = or[o][g];
                String t = (l != null) ? l.toString() : "---";
                if (t.length() > 24) {
                    t = t.substring(0, 24);
                }
                System.out.printf("| %-25s", t);
            }
            System.out.println();
        }
    }

    public void stampaOrarioDocente(Docente docente) {
        System.out.println("\n===== ORARIO DOCENTE " + docente.getNomeCognome() + " =====");
        Lezione[][] or = estraiOrarioDocente(docente);

        System.out.printf("%-6s", "Ora");
        for (int g = 0; g < giorni; g++) {
            System.out.printf("| %-30s", GiorniSettimana.values()[g].getNome());
        }
        System.out.println();
        System.out.println("-".repeat(6 + (33 * giorni)));

        for (int o = 0; o < ore; o++) {
            System.out.printf("%-6s", (o + 1) + "°");
            for (int g = 0; g < giorni; g++) {
                Lezione l = or[o][g];
                String t = (l != null)
                        ? l.getMateria().getNomeMateria() + " (" + l.getClasse().getNomeCompleto() + ")"
                        : "---";
                if (t.length() > 29) {
                    t = t.substring(0, 29);
                }
                System.out.printf("| %-30s", t);
            }
            System.out.println();
        }
    }
}
