package com.AniMng;

import com.AniMng.animation.Animable;
import com.AniMng.animation.Reversable;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

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
    private AnimationManager<T> currentParallel;
    private Runnable pre;
    private Runnable post;
    private int firts;

    public AnimationManager() {
        anilist = new ArrayList<>();
        pre = () -> {
        };
        post = () -> {
        };
        this.currentRate = 1;
        this.firts = -1;
    }

    /**
     *
     * @param objeto (obligatoriamente que hereden de Component) (Preferably
     * Objects that extends from Component)
     */
    public AnimationManager(T objeto) {
        this();
        this.object = objeto;
    }

    public List<Animation> getAnimations() {
        return anilist;
    }

    public void setCurrentParallel(AnimationManager<T> currentParallel) {
        this.currentParallel = currentParallel;
    }

    public AnimationManager<T> getCurrentParallel() {
        return currentParallel;
    }

    public synchronized AnimationManager<T> beginParallel() {
        this.firts = anilist.size();
        return this;
    }

    public synchronized AnimationManager<T> closeParallel() {
        if (this.firts == -1) {
            return this;
        }
        if (this.firts == anilist.size()) {
            this.firts = -1;
            return this;
        }
        ArrayList<Animation> anis = copyAnimations(this.firts, anilist.size(), anilist);
        anilist.removeAll(anis);
        anilist.add(new AnimationParallel(anis).getAnimation());
        this.firts = -1;
        return this;
    }

    private static ArrayList<Animation> copyAnimations(int start, int limit, List<Animation> anilist) {
        ArrayList<Animation> anis = new ArrayList<>();
        for (int i = start; i < limit; i++) {
            anis.add(new Animation(anilist.get(i)));
        }
        return anis;
    }

    public static class AnimationParallel implements Animable {

        private List<Animation> anis;
        private ArrayList<Animation> reverse;
        int min, max, tempCount;
        long reverseRate;

        public AnimationParallel(List<Animation> animations) {
            this.anis = animations;
            reverse = copyAnimations(0, anis.size(), anis);
            Collections.reverse(reverse);
            Stream<Integer> map = anis.stream().map(Animation::getRep);
            max = map.max(Comparator.naturalOrder()).get();
            map = anis.stream().map(Animation::getRep);
            min = map.min(Comparator.naturalOrder()).get();
        }

        public Animation getAnimation() {
            return new Animation(this, max, 0);
        }

        @Override
        public void act(Component object, int currentRep) {
            for (Animation ani : anis) {
                if (currentRep < ani.getRep()) {
                    ani.getAnimable().act(object, currentRep);
                    try {
                        Thread.sleep(ani.getRate());
                    } catch (InterruptedException e) {
                    }
                }
            }
        }

        @Override
        public void reverse(Component object, int currentRep) {
            for (Animation ani : reverse) {
                if (currentRep < ani.getRep()) {
                    ani.getAnimable().reverse(object, currentRep);
                    try {
                        Thread.sleep(ani.getRate());
                    } catch (Exception e) {
                    }
                }
            }
        }

        private void setRate(long rate) {
            reverse.forEach((ani) -> {
                ani.setRate(rate);
            });
        }

        @Override
        public String toString() {
            return "Parallel" + anis;
        }

    }

    private static class Motion implements Animable {

        private Point orientacion;

        public Motion(Point orientacion) {
            this.orientacion = orientacion;
        }

        @Override
        public void act(Component object, int currentRep) {
            object.setLocation(object.getX() + orientacion.x, object.getY() + orientacion.y);
        }

        @Override
        public void reverse(Component object, int currentRep) {
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
        public void act(Component object, int currentRep) {
            reversable.reverse(object, currentRep);
        }

        @Override
        public void reverse(Component object, int currentRep) {
            reversable.act(object, currentRep);
        }

        @Override
        public String toString() {
            return "Reverse:" + reversable.toString();
        }

    }

    private static class Mutation implements Animable, Reversable {

        private Dimension dms;

        public Mutation(Dimension dms) {
            this.dms = dms;
        }

        @Override
        public void act(Component object, int currentRep) {
            object.setSize(object.getWidth() + dms.width, object.getHeight() + dms.height);
        }

        @Override
        public void reverse(Component object, int currentRep) {
            // osea cada uno implementa reverse pero para usarse en Reverse class ;) 
            object.setSize(object.getWidth() - dms.width, object.getHeight() - dms.height);
        }

        @Override
        public String toString() {
            return dms.toString();
        }

    }

    private static class Animation {

        protected static int COUNT;
        protected final int id;
        protected long rate;
        protected int rep;
        protected Animable animable;

        public Animation() {
            this.id = 0;
        }

        private Animation(Animable animable) {
            this(animable, 0, 0);
        }

        private Animation(Animation ani) {
            this(ani.animable, ani.rep, ani.rate);
        }

        private Animation(Animable animable, int rep, long rate) {
            this.rate = rate;
            this.rep = rep;
            this.animable = animable;
            this.id = COUNT++;
        }

        @Override
        public String toString() {
            return "\nAnimation{" + "id=" + id + ", rate=" + rate + ", rep=" + rep + ", animable=" + animable + '}';
        }

        public Animable getAnimable() {
            return animable;
        }

        public void setAnimable(Animable animable) {
            this.animable = animable;
        }

        public long getRate() {
            return rate;
        }

        public void setRate(long rate) {
            this.rate = rate;
        }

        public int getRep() {
            return rep;
        }

        public void setRep(int rep) {
            this.rep = rep;
        }

        public void move(Component object) {
            try {
                for (int i = 0; i < rep; i++) {
                    animable.act(object, i);
                    Thread.sleep(rate);
                }
            } catch (Exception e) {
            }
        }

        Animation getReverse() {
            return new Animation(new Reverse(animable), rep, rate);
        }

        Animation getReverse(long rate) {
            if (animable instanceof AnimationParallel) {
                ((AnimationParallel) animable).setRate(rate);
                return new Animation(new Reverse(animable), rep, 0);
            }
            return new Animation(new Reverse(animable), rep, rate);
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
        anilist.add(new Animation(new Motion(new Point(x, y)), rep, currentRate));
        return this;
    }

    /**
     * Permite añadir una implementación externa de una animación u otro tipo de
     * acción que quiera ser ejecutada como si se tratase de una
     *
     * @param animable la implementación 
     * @param rep el número de veces que se ejecutará 
     * @return este objeto <code>AnimationManager</code>
     */
    public AnimationManager<T> addAction(Animable animable, int rep) {
        anilist.add(new Animation(animable, rep, currentRate));
        return this;
    }

    public AnimationManager<T> addMutation(int width, int height, int rep) {
        anilist.add(new Animation(new Mutation(new Dimension(width, height)), rep, currentRate));
        return this;
    }

    /**
     * Asigna el tiempo de espera para cada objeto Animation añadido
     *
     * @param rate el tiempo de espera para cada iteración de animación
     * @return este objeto <code>AnimationManager</code>
     */
    public AnimationManager<T> setRate(long rate) {
        currentRate = rate;
        return this;
    }

    @Override
    public void run() {
        pre.run();
        anilist.forEach((ani) -> {
            ani.move(object);
        });
        post.run();
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
     *
     * @see #changeRunMode()
     * @see #startLineal()
     * @see #startParallel()
     */
    public void start() {
        start(() -> {
            pre.run();
            anilist.forEach((ani) -> {
                System.out.println(ani);
                ani.move(object);
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
     * único hilo, caso contrario son ejecutadas una seguidas de otras en cada
     * iteración. Este método está orientado a la interpolación de modos de
     * ejecución de las animaciones.
     *
     * @see #start()
     * @see #startLineal()
     * @see #startParallel()
     * @see #
     *
     * @return este objeto <code>AnimationManager</code>
     */
    public AnimationManager<T> changeRunMode() {
        System.out.println("aniSize: " + anilist.size());
        System.out.println("first: " + this.firts);
        System.out.println("parallel: " + isRunModeParallel());
        return isRunModeParallel() ? closeParallel() : beginParallel();
    }

    /**
     * Consulta si el modo de ejecucón es paralela
     *
     * @return false si el modo de ejecicón de las animaciones es en modo lineal
     * (todo en un solo hilo), true si es el modo de ejecución es en paralelo
     */
    public boolean isRunModeParallel() {
        return this.firts != -1 && this.firts <= anilist.size();
    }

    private Thread start(Runnable r) {
        Thread t = new Thread(r);
        t.start();
        return t;
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
            copy.add(ani.getReverse());
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
            copy.add(ani.getReverse(rate));
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
        anilist.add(ani.getReverse(rate));
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
        anilist.add(ani.getReverse());
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
