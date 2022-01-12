package com.AniMng;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author Señor C. y Jarzta
 * @param <T> (De preferencia Objetos que hereden de Component) (Preferably
 * Objects that extends from Component)
 */
public class AnimationManager<T extends Component> implements Runnable {

    private T objeto;
    private List<Animation> anilist;
    private long currentRate;

    public AnimationManager() {
        anilist = new ArrayList<>();
        this.currentRate = 1;
    }

    /**
     *
     * @param objeto (De preferencia Objetos que hereden de Component)
     * (Preferably Objects that extends from Component)
     */
    public AnimationManager(T objeto) {
        this();
        this.objeto = objeto;
    }

    private interface Animable {

        void move(Component object, long rate, int rep);
    }

    private static class Motion implements Animable {

        private Point orientacion;

        public Motion(Point orientacion) {
            this.orientacion = orientacion;
        }

        @Override
        public void move(Component object, long rate, int rep) {
            try {
                for (int i = 0; i < rep; i++) {
                    object.setLocation(object.getX() + orientacion.x, object.getY() + orientacion.y);
                    Thread.sleep(rate);
                }
            } catch (Exception e) {
            }
        }

    }

    private static class Mutation implements Animable {

        private Dimension dms;

        public Mutation(Dimension dms) {
            this.dms = dms;
        }

        @Override
        public void move(Component object, long rate, int rep) {
            try {
                for (int i = 0; i < rep; i++) {
                    object.setSize(object.getWidth() + dms.width, object.getHeight() + dms.height);
                    Thread.sleep(rate);
                }
            } catch (Exception e) {
            }
        }
    }

    private static class Animation {

        private long rate;
        private int rep;
        private Animable animable;

        private Animation(Animable animable, int rep, long rate) {
            this.rate = rate;
            this.rep = rep;
            this.animable = animable;
        }

        private void move(Component object) {
            animable.move(object, rate, rep);
        }

    }

    /**
     * este método utiliza un For en el cual la variable rep determina el numero
     * de veces que se reperirá el bucle, las variables "X" y "Y", se le sumaran
     * a la coordenada "X" y "Y" del Objeto que va a animar en cada ciclo del
     * bucle for, y la variable "rate" serán los milisegundos que se detenga el
     * hilo.
     *
     * @param x pixeles en lo que se moverá la coordenada X.
     * @param y pixeles en lo que se moverá la coordenada Y.
     * @param rep numero de veces que se moverán las coordenadas X y Y la
     * cantidad de pixeles indicada.
     * @param rate esta cantidad se le asignará al metodo Sleep.
     * @return retorna este mismo método.
     */
//    public AnimationManager<T> addAnimation(int x, int y, int rep, int rate) {
//        currentRate = rate;
//        anilist.add(new Animation(new Point(x, y), rep, currentRate));
//        return this;
//    }
    /**
     * este método utiliza un For en el cual la variable rep determina el numero
     * de veces que se reperirá el bucle, las variables "X" y "Y", se le sumaran
     * a la coordenada "X" y "Y" del Objeto que va a animar en cada ciclo del
     * bucle for, no es necesario pasarle el "rate"
     *
     * @param x pixeles en lo que se moverá la coordenada X.
     * @param y pixeles en lo que se moverá la coordenada Y.
     * @param rep numero de veces que se moverán las coordenadas X y Y la.
     * cantidad de pixeles indicada
     * @return retorna este mismo método.
     */
    public AnimationManager<T> addAnimation(int increment1, int increment2, int rep, boolean isMotion) {
        if (isMotion) {
            return addMotion(increment1, increment2, rep);
        }
        return addMutation(increment1, increment2, rep);
    }

    public AnimationManager<T> addMotion(int x, int y, int rep) {
        anilist.add(new Animation(new Motion(new Point(x, y)), rep, currentRate));
        return this;
    }

    public AnimationManager<T> addMutation(int width, int height, int rep) {
        //como es eso? .v
        anilist.add(new Animation(new Mutation(new Dimension(width, height)), rep, currentRate));
        return this;
    }

    public AnimationManager<T> setRate(long rate) {
        currentRate = rate;
        return this;
    }

    @Override
    public void run() {
        anilist.forEach((ani) -> {
            ani.move(objeto);
        });
    }

    /**
     * Inicia la animación o las animaciones agregadas.
     */
    public void start() {
        Thread hilo = new Thread(this);
        hilo.start();
    }

    public void apply(Component object) {
        new Thread(
                () -> {
                    anilist.forEach((ani) -> {
                        ani.move(object);
                    });
                }
        ).start();
    }

}
