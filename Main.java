package homework1_3;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    static StringBuilder log = new StringBuilder();
    static List<String> listFiles = new ArrayList<>();

    public static void main(String[] args) {
        //В папке Games создаем несколько директорий: src, res, savegames, temp.
        createDirectory("C:/Games/", "src");
        createDirectory("C:/Games/", "res");
        createDirectory("C:/Games/", "savegames");
        createDirectory("C:/Games/", "temp");
        //В каталоге src создаем две директории: main, test.
        createDirectory("C:/Games/src/", "main");
        createDirectory("C:/Games/src/", "test");
        //В подкаталоге main создаем два файла: Main.java, Utils.java.
        createFile("C:/Games/src/main/", "Main.java");
        createFile("C:/Games/src/main/", "Utils.java");
        //В каталог res создаем три директории: drawables, vectors, icons.
        createDirectory("C:/Games/res/", "drawables");
        createDirectory("C:/Games/res/", "vectors");
        createDirectory("C:/Games/res/", "icons");
        //В директории temp создаем файл temp.txt.
        createFile("C:/Games/temp/", "temp.txt");
        //Запись в файл лога
        writeLogToFile("C:/Games/temp/temp.txt");
        //Создаем три экземпляра класса GameProgress
        GameProgress gameProgress1 = new GameProgress(20, 35, 2, 45.6);
        GameProgress gameProgress2 = new GameProgress(56, 12, 11, 76);
        GameProgress gameProgress3 = new GameProgress(77, 89, 15, 89.6);
        //Сохраняем сериализованные объекты GameProgress в папку savegames
        saveGame("C:/Games/savegames/save1.dat", gameProgress1);
        saveGame("C:/Games/savegames/save2.dat", gameProgress2);
        saveGame("C:/Games/savegames/save3.dat", gameProgress3);
        //Созданные файлы сохранений из папки savegames запаковываем в архив zip
        zipFiles("C:/Games/savegames/zip.zip", listFiles);
        //Удаление файлов сохранений, лежащих вне архива
        deleteFiles(listFiles);
        //Производим распаковку архива в папке savegames
        openZip("C:/Games/savegames/zip.zip", "C:/Games/savegames/");
        //Производим считывание, десериализацию одного из файлов и выводим в консоль состояние игры
        System.out.println(openProgress("C:/Games/savegames/save3.dat"));
    }

    public static void createDirectory(String pathName, String directoryName) {
        File dir = new File(pathName + directoryName);
        if (dir.mkdir()) {
            log.append(" / " + LocalDateTime.now() + " Создан каталог " + directoryName);
        }
    }

    public static void createFile(String pathName, String fileName) {
        File file = new File(pathName, fileName);
        try {
            if (file.createNewFile()) {
                log.append(" / " + LocalDateTime.now() + " Создан файл " + fileName);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void writeLogToFile(String fileName) {
        try (FileWriter writer = new FileWriter(fileName, false)) {
            writer.write(log.toString());
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void saveGame(String pathName, GameProgress gameProgress) {
        try (FileOutputStream fos = new FileOutputStream(pathName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(gameProgress);
            listFiles.add(pathName);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void zipFiles(String pathName, List<String> list) {
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(pathName))) {
            for (String file : list) {
                FileInputStream fis = new FileInputStream(file);
                ZipEntry entry = new ZipEntry(file);
                zout.putNextEntry(entry);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                zout.write(buffer);
                zout.closeEntry();
                fis.close();
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void deleteFiles(List<String> list) {
        for (String file : list) {
            File fileList = new File(file);
            if (fileList.delete()) {
                System.out.println("Файл успешно удален");
            } else {
                System.out.println("Файл не может быть удален");
            }
        }
    }

    public static void openZip(String pathZip, String pathUnzip) {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(pathZip))) {
            ZipEntry entry;
            String name;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName();
                byte[] buffer = new byte[1024];
                FileOutputStream fout = new FileOutputStream(name);
                int c;
                while ((c = zin.read(buffer)) > 0) {
                    fout.write(buffer);
                }
                fout.flush();
                fout.close();
            }
            zin.closeEntry();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static GameProgress openProgress(String pathName) {
        GameProgress gameProgress = null;
        try (FileInputStream fis = new FileInputStream(pathName);
             ObjectInputStream ois = new ObjectInputStream((fis))) {
            gameProgress = (GameProgress) ois.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return gameProgress;
    }
}
