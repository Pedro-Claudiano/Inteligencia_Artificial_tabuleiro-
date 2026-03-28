
package main;

    public class Movimento {
    public int lOrigem, cOrigem, lDestino, cDestino;
    public char idOrigem, idDestino;
    public boolean ehCaptura;

    public Movimento(int lO, int cO, int lD, int cD, char idO, char idD, boolean cap) {
        this.lOrigem = lO; this.cOrigem = cO;
        this.lDestino = lD; this.cDestino = cD;
        this.idOrigem = idO; this.idDestino = idD;
        this.ehCaptura = cap;
    }
}