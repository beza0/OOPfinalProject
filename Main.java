import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Main {
    private JFrame frame;

    public Main() {
        // JFrame oluşturulması ve ayarlanması
        frame = new JFrame("Dosya Taşıma Uygulaması");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        // Ana panel oluşturulması
        JPanel panel = new JPanel(new BorderLayout());

        // Etiket oluşturulması ve ayarlanması
        JLabel label = new JLabel("Hangisini taşımak istiyorsunuz?", SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);

        // Buton paneli oluşturulması
        JPanel buttonPanel = new JPanel(new FlowLayout());

        // Butonların oluşturulması
        JButton txtButton = new JButton("TXT Dosyası");
        JButton pngButton = new JButton("PNG Dosyası");
        JButton pdfButton = new JButton("PDF Dosyası");
        JButton tumuButton = new JButton("Tümü");

        // Butonların panel üzerine eklenmesi
        buttonPanel.add(txtButton);
        buttonPanel.add(pngButton);
        buttonPanel.add(pdfButton);
        buttonPanel.add(tumuButton);

        // Buton panelinin ana panele eklenmesi
        panel.add(buttonPanel, BorderLayout.CENTER);

        // Ana panelin JFrame'e eklenmesi ve görünür yapılması
        frame.add(panel);
        frame.setVisible(true);

        // TXT butonunun ActionListener'ı
        txtButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DosyaTasimaIslemi txtTasima = new DosyaTasimaIslemi("txt");
                txtTasima.tasimaSecimiYap();
            }
        });

        // PNG butonunun ActionListener'ı
        pngButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DosyaTasimaIslemi pngTasima = new DosyaTasimaIslemi("png");
                pngTasima.tasimaSecimiYap();
            }
        });

        // PDF butonunun ActionListener'ı
        pdfButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DosyaTasimaIslemi pdfTasima = new DosyaTasimaIslemi("pdf");
                pdfTasima.tasimaSecimiYap();
            }
        });

        // Tümü butonunun ActionListener'ı
        tumuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DosyaTasimaIslemi tumuTasima = new DosyaTasimaIslemi("tumu");
                tumuTasima.tasimaSecimiYap();
            }
        });
    }

    public static void main(String[] args) {
        // Uygulamanın başlatılması
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().frame.setVisible(true);
            }
        });
    }
}

class DosyaTasimaIslemi {
    private String dosyaTipi;

    public DosyaTasimaIslemi(String dosyaTipi) {
        this.dosyaTipi = dosyaTipi;
    }

    public void tasimaSecimiYap() {
        // Kaynak dizin seçim işlemleri
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Kaynak Dizin Seçimi");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File kaynakDizin = fileChooser.getSelectedFile();

            // Hedef dizin seçim işlemleri
            fileChooser.setDialogTitle("Hedef Dizin Seçimi");
            result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File hedefDizin = fileChooser.getSelectedFile();

                String message = "Dosyaları hedef dizine taşımak istiyor musunuz?";

                int choice = JOptionPane.showConfirmDialog(null, message, "Taşıma Onayı", JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    // Dosya taşıma işlemi
                    File[] dosyalar;

                    if (dosyaTipi.equals("tumu")) {
                        dosyalar = kaynakDizin.listFiles();
                    } else {
                        dosyalar = kaynakDizin.listFiles((dir, name) -> name.toLowerCase().endsWith("." + dosyaTipi));
                    }

                    if (dosyalar != null) {
                        for (File dosya : dosyalar) {
                            String dosyaAdi = dosya.getName();

                            // Gizli dosya yapma seçeneği kontrol edilir
                            boolean gizliDosya = gizliDosyaSor();
                            if(gizliDosya==true) {
                                dosyaAdi = "(gizli)" + dosyaAdi;
                            }

                            File hedefDosya = new File(hedefDizin.getAbsolutePath() + File.separator + dosyaAdi);

                            // Dosya taşıma işlemi
                            if (gizliDosya) {
                                try {
                                    gizliTaşıma(dosya, hedefDosya);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                try {
                                    normalTaşıma(dosya, hedefDosya);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }

                    // İşlem tamamlandı mesajı gösterilir
                    JOptionPane.showMessageDialog(null, "Dosyalar taşındı. İşlem tamamlandı.");
                }
            }
        }
    }

    private void normalTaşıma(File kaynakDosya, File hedefDosya) throws IOException {
        // Normal dosya taşıma işlemi
        Path kaynakPath = kaynakDosya.toPath();
        Path hedefPath = hedefDosya.toPath();

        Files.move(kaynakPath, hedefPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private void gizliTaşıma(File kaynakDosya, File hedefDosya) throws IOException {

        // Dosyayı hedef konuma taşıma
        Path kaynakPath = kaynakDosya.toPath();
        Path hedefPath = hedefDosya.toPath();

        Files.move(kaynakPath, hedefPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private boolean gizliDosyaSor() {
        // Gizli dosya yapma seçeneğinin sorulması
        boolean gizlemesecimi = JOptionPane.showConfirmDialog(null, "Dosyaları gizli yapmak istiyor musunuz?", "Gizlilik", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

        return gizlemesecimi;
    }


}
