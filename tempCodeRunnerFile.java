
    public static void main(String[] args) {
        try {
            ConexionDB.crearTablas();
            System.out.println("Tablas creadas");
        } catch (Exception e) {
            e.printStackTrace();
        }
}