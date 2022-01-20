package demo;

import com.AniMng.AnimationManager;
import com.AniMng.animation.Animable;
import java.awt.Color;
import java.awt.Component;

/**
 *
 * @author carlo
 */
public class AnimableDemo extends javax.swing.JFrame {

    /**
     * Creates new form MainFrame
     */
    public AnimableDemo() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Animation Manager Demo");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelFondo = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 102, 102));
        setMinimumSize(new java.awt.Dimension(640, 480));

        jPanelFondo.setBackground(new java.awt.Color(51, 51, 51));
        jPanelFondo.setLayout(null);

        jLabel1.setBackground(new java.awt.Color(153, 0, 255));
        jLabel1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Texto de Ejemplo");
        jLabel1.setOpaque(true);
        jPanelFondo.add(jLabel1);
        jLabel1.setBounds(240, 230, 257, 56);

        jLabel2.setBackground(new java.awt.Color(0, 204, 204));
        jLabel2.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 51));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText(" :)");
        jLabel2.setToolTipText("");
        jLabel2.setOpaque(true);
        jPanelFondo.add(jLabel2);
        jLabel2.setBounds(80, 140, 85, 79);

        jButton1.setBackground(new java.awt.Color(102, 102, 255));
        jButton1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButton1.setText("start");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanelFondo.add(jButton1);
        jButton1.setBounds(230, 360, 80, 40);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelFondo, javax.swing.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelFondo, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        example();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AnimableDemo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AnimableDemo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AnimableDemo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AnimableDemo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AnimableDemo().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanelFondo;
    // End of variables declaration//GEN-END:variables

    private void example() {
        Animable action = new Animable() {
            private Color defaultColor = jLabel1.getBackground();
            private Color color = Color.CYAN;
            
            @Override
            public void act(Component object, int currentRep) {
                if(object.getBackground().equals(color)) {
                    object.setBackground(defaultColor);
                    return;
                }
                object.setBackground(color);
            }

            @Override
            public void reverse(Component object, int currentRep) {
                object.setBackground(defaultColor);
            }
        };        
        
        AnimationManager am = new AnimationManager(jLabel1);
        am.setPre(() -> {
            jButton1.setEnabled(false);
        });
        am.setPost(() -> {
            jButton1.setEnabled(true);
        });

        /**
         * el modo por defecto es iteraciones independientes para cada
         * animación, con changeRunMode en los valores por defecto cada
         * animación agregada es ejecutada de forma secuencial en cada iteración
         */
        am.changeRunMode()
                .setRate(10)
                .addMotion(-1, 0, 100)
                .setRate(5)
                .addMotion(1, 1, 100)
                .addMutation(-1, 1, 100)
                .setRate(10)
                .addAction(action, 50)
                .changeRunMode()
                .setRate(5)
                .addMotion(-1, -1, 200)
                .addMutation(1, 1, 50)
                .extendReverse(1)
                .start();
    }
}
