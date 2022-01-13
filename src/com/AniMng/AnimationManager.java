package com.AniMng;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AnimationManager es un "administrador de animaciones" que se encarga de
 * ejecutar movimientos y tranformacionas con distintos parámetros.
 *
 * @author Señor C. y Jarzta
 * @param <T> (De preferencia Objetos que hereden de Component) (Preferably
 * Objects that extends from Component)
 */
public class AnimationManager<T extends Component> implements Runnable {

    private T object;
    private List<Animation> anilist;
    private long currentRate;
    private boolean currentRunMode;
    private Runnable pre;
    private Runnable post;

    public AnimationManager() {
        anilist = new ArrayList<>();
        pre = ()->{};
        post = ()->{};
        this.currentRate = 1;
    }

    /**
     *
     * @param objeto (obligatoriamente que hereden de Component)
     * (Preferably Objects that extends from Component)
     */
    public AnimationManager(T objeto) {
        this();
        this.object = objeto;
    }

    public List<Animation> getAnimations() {
        return anilist;
    }

    public void setRunModeParallel(boolean runModeParallel) {
        currentRunMode = runModeParallel;
    }

    private interface Animable extends Movible, Reversable {
    }

    private interface Movible {

        void move(Component object);
    }

    private interface Reversable {

        void reverse(Component object);
    }

    private static class Motion implements Animable {

        private Point orientacion;

        public Motion(Point orientacion) {
            this.orientacion = orientacion;
        }

        @Override
        public void move(Component object) {
            object.setLocation(object.getX() + orientacion.x, object.getY() + orientacion.y);
        }

        @Override
        public void reverse(Component object) {

            object.setLocation(object.getX() - orientacion.x, object.getY() - orientacion.y);
        }

        @Override
        public String toString() {
            return orientacion.toString();
        }

    }

    private static class Reverse implements Animable {

        private Animable reversable;

        public Reverse(Animable reversable) {
            this.reversable = reversable;
        }

        @Override
        public void move(Component object) {
            reversable.reverse(object);
        }

        @Override
        public void reverse(Component object) {
            reversable.move(object);
        }

    }

    private static class Mutation implements Animable, Reversable {

        private Dimension dms;

        public Mutation(Dimension dms) {
            this.dms = dms;
        }

        @Override
        public void move(Component object) {
            object.setSize(object.getWidth() + dms.width, object.getHeight() + dms.height);
        }

        @Override
        public void reverse(Component object) {
            // osea cada uno implementa reverse pero para usarse en Reverse class ;) 
            object.setSize(object.getWidth() - dms.width, object.getHeight() - dms.height);
        }

        @Override
        public String toString() {
            return dms.toString();
        }

    }

    private static class Animation {

        private static int COUNT;
        private final int id;
        private long rate;
        private int rep;
        private Animable animable;
        private boolean parallel;

        private Animation(Animable animable, int rep, long rate) {
            this(animable, rep, rate, false);
        }

        private Animation(Animable animable, int rep, long rate, boolean parallel) {
            this.rate = rate;
            this.rep = rep;
            this.animable = animable;
            this.parallel = parallel;
            this.id = COUNT++;
        }

        private void move(Component object) {
            try {
                // hasta aquí sabes que es la wea de la interface Animable no? :v  si, ya pues Reverse también la implementa, pero para usar el método reverse :)
                for (int i = 0; i < rep; i++) {
                    animable.move(object);
                    Thread.sleep(rate);
                }
            } catch (Exception e) {
            }
        }

        public boolean isRunModeParallel() {
            return parallel;
        }

        public void setRunModeParallel(boolean isParallel) {
            this.parallel = isParallel;
        }

        @Override
        public String toString() {
            return "\nAnimation{" + "id=" + id + ", rate=" + rate + ", rep=" + rep + ", animable=" + animable + ", parallel=" + parallel + '}';
        }

        public Animable getAnimable() {
            return animable;
        }

        public void setAnimable(Animable animable) {
            this.animable = animable;
        }

        @Override
        public Animation clone() throws CloneNotSupportedException {
            Animation a = new Animation(animable, rep, rate, parallel);
            return a;
        }

    }

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
        anilist.add(new Animation(new Motion(new Point(x, y)), rep, currentRate, currentRunMode));
        return this;
    }

    public AnimationManager<T> addMutation(int width, int height, int rep) {
        anilist.add(new Animation(new Mutation(new Dimension(width, height)), rep, currentRate, currentRunMode));
        return this;
    }

    public AnimationManager<T> setRate(long rate) {
        currentRate = rate;
        return this;
    }

    @Override
    public void run() {
        anilist.forEach((ani) -> {
            ani.move(object);
        });
    }

    /**
     * Inicia la animación o las animaciones agregadas en un solo
     * <code>Thread</code>.
     *
     * @see #startParallel()
     * @see #start()
     */
    public void startLineal() {
        Thread hilo = new Thread(this);
        hilo.start();
    }

    /**
     * Inicia el conjunto de animaciones en <code>Thread</code>'s independientes
     * dentro en un solo <code>Thread</code>.
     *
     * @see #startLineal()
     * @see #start()
     */
    public void startParallel() {
        start(() -> {
            anilist.forEach((ani) -> {
                start(() -> {
                    ani.move(object);
                });
            });
        });
    }

    /**
     * Inicia el conjunto de animaciones respetando los valores de modo de
     * ejecucución asignados para cada animación
     * @see #changeRunMode() 
     * @see #startLineal()  
     * @see #startParallel()   
     */
    public void start() {
        start(() -> {
            pre.run();
            anilist.forEach((ani) -> {
                if (ani.isRunModeParallel()) {
                    start(() -> {
                        ani.move(object);
                    });
                } else {
                    ani.move(object);
                }
            });
            post.run();
        });
    }

    /**
     * Cambia el modo en el que se ejecutan las animaciones que son agregadas
     * con
     * {@link #addMotion(int, int, int)}, {@link #addMutation(int, int, int)}, {@link #addAnimation(int, int, int, boolean)}
     * después de su invocación. Existen dos modos: el paralelo y el lineal si
     * changeRunMode está en falso las animaciones son ejecutadas todas en un
     * único hilo, caso contrario son ejecutadas cada una en un hilo
     * independiente que a su vez están en otro hilo. Este método está orientado
     * a la interpolación de modos de ejecución de las animaciones.
     *
     * @see #start()
     * @see #startLineal()
     * @see #startParallel()
     * @see #
     *
     * @return este objeto <code>AnimationManager</code>
     */
    public AnimationManager<T> changeRunMode() {
        setRunModeParallel(!currentRunMode);
        return this;
    }

    /**
     * Consulta si el modo de ejecucón es paralela
     *
     * @return false si el modo de ejecicón de las animaciones es en modo lineal
     * (todo en un solo hilo), true si es el modo de ejecución es en paralelo
     */
    public boolean isRunModeParallel() {
        return currentRunMode;
    }

    private void start(Runnable r) {
        new Thread(r).start();
    }

    public void apply(Component object) {
        start(() -> {
            anilist.forEach((ani) -> {
                ani.move(object);
            });
        });
    }

    public AnimationManager<T> newWithObject() {
        return new AnimationManager<>(object);
    }

    /**
     * Agrega el equivalenete inverso de todo el conjunto de animaciones
     * presentes antes de su invoción a la lista de animaciones
     *
     * @return este objeto <code>AnimationManager</code>
     */
    public AnimationManager<T> extendReverse() {
        ArrayList<Animation> copy = new ArrayList<>();
        anilist.forEach((ani) -> {
            copy.add(new Animation(new Reverse(ani.getAnimable()), ani.rep, ani.rate));
        });
        Collections.reverse(copy);
        copy.forEach((ani) -> {
            anilist.add(ani);
        });
        return this;
    }

    /**
     * Igual que {@link #extendReverse()} con la exepción que permite asignar el
     * tiempo de espera entre cada iteración de animación, dicho tiempo es
     * aplicado a todo el conjunto de animaciones.
     *
     * @param rate el tiempo de espera que se aplicará a todas las animaciones.
     * @return este objeto <code>AnimationManager</code>
     */
    public AnimationManager<T> extendReverse(long rate) {
        ArrayList<Animation> copy = new ArrayList<>();
        anilist.forEach((ani) -> {
            copy.add(new Animation(new Reverse(ani.getAnimable()), ani.rep, rate));
        });
        Collections.reverse(copy);
        copy.forEach((ani) -> {
            anilist.add(ani);
        });
        return this;
    }

    /**
     * Igual que {@link #addReverse(int)} con la exepción que permite asignar el
     * tiempo de espera entre cada iteración de animación, dicho tiempo es
     * aplicado a todo el conjunto de animaciones.
     *
     * @param index el indice de la animación.
     * @param rate el tiempo de espera que se aplicará a todas las animaciones.
     * @return este objeto <code>AnimationManager</code>
     */
    public AnimationManager<T> addReverse(int index, long rate) {
        Animation ani = anilist.get(index);
        if (index < 0 || index >= anilist.size() || ani == null) {
            return this;
        }
        anilist.add(new Animation(new Reverse(ani.getAnimable()), ani.rep, rate));
        return this;
    }

    /**
     * Añade la animación inversa equivalente de la encontrada en la lista de
     * animaciones
     *
     * @param index
     * @return este objeto <code>AnimationManager</code>
     */
    public AnimationManager<T> addReverse(int index) {
        Animation ani = anilist.get(index);
        if (index < 0 || index >= anilist.size() || ani == null) {
            return this;
        }
        anilist.add(new Animation(new Reverse(ani.getAnimable()), ani.rep, ani.rate));
        return this;
    }

    @Override
    protected AnimationManager<T> clone() throws CloneNotSupportedException {
        super.clone();
        AnimationManager<T> am = new AnimationManager<>(object);
        am.anilist = (List<Animation>) ((ArrayList<Animation>) anilist).clone();
        return am;
    }

    public Runnable getPre() {
        return pre;
    }

    /**
     * Permite añadir implmentación antes de iniciar la animación
     *
     * @param pre la implementación
     */
    public void setPre(Runnable pre) {
        this.pre = pre;
    }

    public Runnable getPost() {
        return post;
    }

    /**
     * Permite añadir implmentación después de iniciar la animación
     *
     * @param post la implentación
     */
    public void setPost(Runnable post) {
        this.post = post;
    }

}
