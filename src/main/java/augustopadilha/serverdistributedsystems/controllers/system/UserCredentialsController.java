package augustopadilha.serverdistributedsystems.controllers.system;

public class UserCredentialsController {
        public static boolean isValidEmailFormat(String email) {
            if (email == null) {
                return false;
            }
            String emailRegex = "^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$";
            return email.matches(emailRegex);
        }

        public static boolean validate(String email, String password) {
            // Verificar se o email e a senha são válidos
            if (email == null || password == null) {
                System.out.println("Erro ao processar o JSON: um ou mais campos estão faltando.");
                System.out.println("Email: " + email);
                System.out.println("Senha: " + password);

                return false;
            }

            // Verificar se o email está em um formato válido
            if (!isValidEmailFormat(email)) {
                System.out.println("Erro ao processar o JSON: o email não está em um formato válido.");
                return false;
            }
            return true;
        }
    }