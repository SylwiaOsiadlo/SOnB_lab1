public class zad2 {

    public static void main(String[] args) {
        java.nio.file.Path sciezka;
        try {
            sciezka = (args.length > 0)
                    ? java.nio.file.Paths.get(args[0])
                    : java.nio.file.Paths.get("data.txt");
        } catch (SecurityException e) {
            blad("Ograniczenia bezpieczeństwa blokują dostęp do ścieżki.", e);
            return;
        } catch (RuntimeException e) {
            blad("Nieprawidłowa ścieżka pliku.", e);
            return;
        }
        try {
            java.nio.file.Path parent = sciezka.getParent();
            if (parent != null) {
                java.nio.file.Files.createDirectories(parent);
            }
        } catch (java.io.IOException e) {
            blad("Błąd wejścia/wyjścia przy tworzeniu katalogów.", e);
            return;
        } catch (SecurityException e) {
            blad("Brak uprawnień do utworzenia katalogów.", e);
            return;
        }

        String znacznikCzasu = java.time.ZonedDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS z")
                        .withZone(java.time.ZoneId.systemDefault())
        );
        String linia = "Zapisano: " + znacznikCzasu + System.lineSeparator();

        try (java.io.BufferedWriter w = java.nio.file.Files.newBufferedWriter(
                sciezka,
                java.nio.charset.StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.TRUNCATE_EXISTING,
                java.nio.file.StandardOpenOption.WRITE)) {

            w.write(linia);
            w.flush();
            System.out.println("OK: Zapisano do pliku: " + sciezka.toAbsolutePath());

        } catch (java.io.IOException e) {
            blad("Nie udało się zapisać do pliku (sprawdź uprawnienia i ścieżkę).", e);
            return;
        } catch (SecurityException e) {
            blad("Ograniczenia bezpieczeństwa blokują zapis do pliku.", e);
            return;
        }

        try {
            if (!java.nio.file.Files.exists(sciezka) || !java.nio.file.Files.isRegularFile(sciezka)) {
                System.err.println("BŁĄD: Ścieżka nie wskazuje na istniejący zwykły plik: "
                        + sciezka.toAbsolutePath());
                return;
            }

            try (java.io.BufferedReader r = java.nio.file.Files.newBufferedReader(
                    sciezka, java.nio.charset.StandardCharsets.UTF_8)) {
                System.out.println("Zawartość pliku:");
                String line;
                while ((line = r.readLine()) != null) {
                    System.out.println(line);
                }
            }

        } catch (java.io.IOException e) {
            blad("Nie udało się odczytać pliku (sprawdź uprawnienia i stan dysku).", e);
        } catch (SecurityException e) {
            blad("Ograniczenia bezpieczeństwa blokują odczyt pliku.", e);
        }
    }

    private static void blad(String komunikat, Throwable e) {
        System.err.println("BŁĄD: " + komunikat);
        String msg = e.getMessage();
        if (msg != null && !msg.isBlank()) {
            System.err.println("  ↳ " + e.getClass().getSimpleName() + ": " + msg);
        } else {
            System.err.println("  ↳ " + e.getClass().getSimpleName());
        }
    }
}
