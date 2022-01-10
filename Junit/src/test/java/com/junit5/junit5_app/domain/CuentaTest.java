package com.junit5.junit5_app.domain;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.junit5.junit5_app.exceptions.DineroInsuficienteException;

class CuentaTest {
	
	private Cuenta cuenta;
	
	private TestInfo testInfo; 
	private TestReporter testReporter;
	
	@BeforeEach
	void initMetodoTest(TestInfo testInfo, TestReporter testReporter) {
		this.testInfo = testInfo;
		this.testReporter = testReporter;
		
		this.cuenta = new Cuenta("Cris", new BigDecimal(1000));
		//System.out.println("Iniciando método...");
	}
	
	@AfterEach
	void endMetodoTest() {
		//System.out.println("Método finalizado");
	}
	
	@BeforeAll
	static void beforeAll() {
		//System.out.println("Antes de instanciar la clase");
	}
	
	@AfterAll
	static void afterAll() {
		//System.out.println("Despues de acabar la clase");
	}

	@Tag("cuenta")
	@Test
	@DisplayName("Probando nombre de la cuenta: valor esperado")
	void testNombreCuenta() {
		//Cuenta cuenta = new Cuenta("Cris", new BigDecimal(1000));
		
		System.out.println("Ejecutando: Nombre: "+ this.testInfo.getDisplayName() +
							" Metodo: " + this.testInfo.getTestMethod());
		
		if(this.testInfo.getTags().contains("cuenta")) {
			System.out.println("Etiqueta cuenta");
		}
		
		this.testReporter.publishEntry("Imprimo en el log de JUnit");
		
		String esperado = "Cris";
		String real = cuenta.getPersona();
		
		//En este caso el String con el mensaje siempre siempre se construye
		//Aunque no se muestre porque la prueba se ejecute ok
		//Pero está gastando recursos
		Assertions.assertEquals(esperado, real, "El nombre de la cuenta no es el esperado: "+ esperado);
		
		//Usando las expresiones lamda
		//ya solo se construye si diera el error
		Assertions.assertEquals(esperado, real, () -> "El nombre de la cuenta no es el esperado: "+ esperado);
		
		//Assertions.assertTrue(real.equals(esperado));
		//fail("Not yet implemented");
	}
	
	@Test
	@DisplayName("Probando saldo de la cuenta: valor esperado, mayor que cero")
	void testSaldoCuenta() {
		//Cuenta cuenta = new Cuenta("Cris", new BigDecimal(1000));
		
		BigDecimal esperado = new BigDecimal(1000);
		BigDecimal real = cuenta.getSaldo();
		
		Assertions.assertEquals(esperado.doubleValue(), real.doubleValue());
		
		Assertions.assertFalse(real.compareTo(BigDecimal.ZERO) < 0);
		
		Assertions.assertTrue(real.compareTo(BigDecimal.ZERO) > 0);
	}

	@Test
	@DisplayName("Probando cuentas iguales: mismos datos")
	void testReferenciaCuenta() {
		//Cuenta cuenta = new Cuenta("Cris", new BigDecimal(1000));
		Cuenta cuenta2 = new Cuenta("Cris", new BigDecimal(1000));
		
		Assertions.assertEquals(cuenta, cuenta2);
	}
	
	@Test
	@DisplayName("Probando debito de la cuenta: not null y valor esperado")
	void testDebitoCuenta() {
		//Cuenta cuenta = new Cuenta("Cris", new BigDecimal(1000));
		cuenta.debito(new BigDecimal(100));
		
		BigDecimal esperado = new BigDecimal(900);
		BigDecimal real = cuenta.getSaldo();
		
		Assertions.assertNotNull(cuenta.getSaldo());
		Assertions.assertEquals(esperado.doubleValue(), real.doubleValue());
		
	}
	
	static List<String> listarMonto() {
		return Arrays.asList("100", "200", "3000");
	}
	
	@Test
	@DisplayName("Probando credito de la cuenta: not null y valor esperado")
	void testCreditoCuenta() {
		//Cuenta cuenta = new Cuenta("Cris", new BigDecimal(1000));
		cuenta.credito(new BigDecimal(100));
		
		BigDecimal esperado = new BigDecimal(1100);
		BigDecimal real = cuenta.getSaldo();
		
		Assertions.assertNotNull(cuenta.getSaldo());
		Assertions.assertEquals(esperado.doubleValue(), real.doubleValue());
		
	}
	
	@Test
	@DisplayName("Probando excepcion saldo insuficiente")
	void testDineroInsuficienteException() {
		//Cuenta cuenta = new Cuenta("Cris", new BigDecimal(1000));
		
		Exception exception = Assertions.assertThrows(DineroInsuficienteException.class, ()-> {
			cuenta.debito(new BigDecimal(2000));
		});
		
		String real = exception.getMessage();
		String esperado = "Saldo insuficiente";
		
		Assertions.assertEquals(real, esperado);
	}
	
	@Test
	@DisplayName("Probando transferencia entre cuentas: valores esperados")
	void testTransferirDineroCuentas() {
		//Cuenta cuenta = new Cuenta("Cris", new BigDecimal(1000));
		Cuenta destino = new Cuenta("Eliz", new BigDecimal(1000));
		
		Banco banco = new Banco();
		banco.setNombre("Triodos");
		banco.transferir(cuenta, destino, new BigDecimal(100));
		
		BigDecimal esperado_origen = new BigDecimal(900);
		BigDecimal esperado_destino = new BigDecimal(1100);
		
		BigDecimal real_origen = cuenta.getSaldo();
		BigDecimal real_destino = destino.getSaldo();
		
		Assertions.assertEquals(esperado_origen, real_origen);
		Assertions.assertEquals(esperado_destino, real_destino);
	}
	
	@Test
	@Disabled
	@DisplayName("Probando relacion Cuenta-Banco y Banco-Cuentas")
	void testRelacionBancoCuenta() {
		//Cuenta origen = new Cuenta("Cris", new BigDecimal(1000));
		Cuenta destino = new Cuenta("Eliz", new BigDecimal(1000));
		
		Banco banco = new Banco();
		banco.addCuenta(cuenta);
		banco.addCuenta(destino);
		banco.setNombre("Triodos");
		
		//Me sirve para ejecutar todos los assert que tengo en un metodo
		//Si los ejecuto uno a uno, si el primero falla, los siguientes no se llegan a ejecutar
		//Con all, los ejecuta todos aunque falle alguna
		Assertions.assertAll(
				()->{ Assertions.assertEquals(2, banco.getCuentas().size()); },
				()->{ Assertions.assertEquals(banco.getNombre(), cuenta.getBanco().getNombre()); },
				()->{ Assertions.assertEquals(cuenta.getPersona(), 
						banco.getCuentas().stream().filter(c -> 
						c.getPersona().equals(cuenta.getPersona()))
						.findFirst()
						.get().getPersona()); },
				()->{ Assertions.assertTrue(banco.getCuentas().stream()
						.anyMatch(c -> c.getPersona().equals(cuenta.getPersona()))); }
		);
	}
	
	@Test
	void testSaldoCuentaDev() {
		boolean esDev = "dev".equals(System.getProperty("ENV"));
		
		//Si es desarrollo, entonces se ejecuta lo siguiente
		//Sino aqui para
		Assumptions.assumeTrue(esDev);
		
		BigDecimal esperado = new BigDecimal(1000);
		BigDecimal real = cuenta.getSaldo();
		
		Assertions.assertEquals(esperado.doubleValue(), real.doubleValue());
		Assertions.assertFalse(real.compareTo(BigDecimal.ZERO) < 0);
		Assertions.assertTrue(real.compareTo(BigDecimal.ZERO) > 0);
	}
	
	@Test
	void testSaldoCuentaDev2() {
		boolean esDev = "dev".equals(System.getProperty("ENV"));
		
		BigDecimal esperado = new BigDecimal(1000);
		BigDecimal real = cuenta.getSaldo();
		
		//Otra forma de hacer lo mismo que la funcion de antes
		Assumptions.assumingThat(esDev, ()-> {
			Assertions.assertEquals(esperado.doubleValue(), real.doubleValue());
		});
		
		//Estas se ejecutan aunque el Assume no sea valido
		Assertions.assertFalse(real.compareTo(BigDecimal.ZERO) < 0);
		Assertions.assertTrue(real.compareTo(BigDecimal.ZERO) > 0);
	}
	
	@DisplayName("Probando repeticiones")
	@RepeatedTest(value=5,name="Repeticion {currentRepetition} de {totalRepetitions}")
	void testRepeticion( RepetitionInfo info) {
		System.out.println("Test repeticion: " + info.getCurrentRepetition() + " de " +info.getTotalRepetitions());
	}
	
	@Tag("parametrizadas")
	@Nested
	@DisplayName("Pruebas parametrizadas")
	class Parametrizados {
		
		@ParameterizedTest
		@ValueSource(strings = {"100", "200", "3000"})
		//@ValueSource(doubles = {100.0, 200.0, 3000.0})
		@DisplayName("Probando debito de la cuenta con parámetros")
		void testDebitoCuentaParametrizado(String monto) {
			//Cuenta cuenta = new Cuenta("Cris", new BigDecimal(1000));
			cuenta.debito(new BigDecimal(monto));
			
			BigDecimal esperado = new BigDecimal(900);
			BigDecimal real = cuenta.getSaldo();
			
			Assertions.assertNotNull(cuenta.getSaldo());
			Assertions.assertEquals(esperado.doubleValue(), real.doubleValue());
			
		}
		
		@ParameterizedTest(name = "CSV SOURCE: varios valores")
		@CsvSource({"1000,100,100", "250,200,300", "300,3000,600"}) //valor,valor,valor
		void testDebitoCuentaParametrizado1(String valor1, String valor2, String valor3) {
			//Cuenta cuenta = new Cuenta("Cris", new BigDecimal(1000));
			cuenta.debito(new BigDecimal(valor1));
			
			BigDecimal esperado = new BigDecimal(valor2);
			BigDecimal real = cuenta.getSaldo();
			
			Assertions.assertNotNull(cuenta.getSaldo());
			Assertions.assertEquals(esperado.doubleValue(), real.doubleValue());
			
		}
		
		@ParameterizedTest(name = "CSV SOURCE: numero {index} ejecutando con valor {0} - {argumentsWithNames}")
		//@CsvSource({"1,100", "2,200", "3,3000"}) //valor,valor
		@CsvSource({"1,100", "2,200", "3,3000"}) //index,valor
		void testDebitoCuentaParametrizado2(String index, String monto) {
			//Cuenta cuenta = new Cuenta("Cris", new BigDecimal(1000));
			cuenta.debito(new BigDecimal(monto));
			
			BigDecimal esperado = new BigDecimal(900);
			BigDecimal real = cuenta.getSaldo();
			
			Assertions.assertNotNull(cuenta.getSaldo());
			Assertions.assertEquals(esperado.doubleValue(), real.doubleValue());
			
		}
		
		@ParameterizedTest(name = "CsvFileSource: numero {index} ejecutando con valor {0} - {argumentsWithNames}")
		@CsvFileSource(resources = "/data.csv") //fichero
		void testDebitoCuentaParametrizado3(String monto) {
			//Cuenta cuenta = new Cuenta("Cris", new BigDecimal(1000));
			cuenta.debito(new BigDecimal(monto));
			
			BigDecimal esperado = new BigDecimal(900);
			BigDecimal real = cuenta.getSaldo();
			
			Assertions.assertNotNull(cuenta.getSaldo());
			Assertions.assertEquals(esperado.doubleValue(), real.doubleValue());
			
		}
		
		@ParameterizedTest(name = "MethodSource: numero {index} ejecutando con valor {0} - {argumentsWithNames}")
		@MethodSource("listarMonto") //fichero
		void testDebitoCuentaParametrizado4(String monto) {
			//Cuenta cuenta = new Cuenta("Cris", new BigDecimal(1000));
			cuenta.debito(new BigDecimal(monto));
			
			BigDecimal esperado = new BigDecimal(900);
			BigDecimal real = cuenta.getSaldo();
			
			Assertions.assertNotNull(cuenta.getSaldo());
			Assertions.assertEquals(esperado.doubleValue(), real.doubleValue());
			
		}
		
	}
	
	
	@Nested
	@Tag("sistema")
	@DisplayName("Probando propiedades del sistema")
	class Sistema {
		
		@Test
		@EnabledOnOs(OS.WINDOWS)
		void testSoloWindows() {
			System.out.println("Estoy en Windows");
		}
		
		@Test
		void imprimirSystemProperties() {
			//Aqui podemos ver todas las propiedades del sistema
			//Y podemos ejecutar unas cosas u otras en funcion de ello
			Properties properties = System.getProperties();
			//properties.forEach((key, value)-> System.out.println("Key: "+ key + " - Value: "+value));
		}
		
		@Test
		@EnabledIfSystemProperty(named = "java.version", matches="1.8.0_201")
		void testJavaVersion() {
			System.out.println("Propiedad del sistema");
		}
		
		//Me puede valer para habilitar pruebas solo para dev o prod
			//Puedo configurar mis propias variables de entorno
		
		@Test
		void imprimirEnviromentVariables() {
			//Aqui podemos ver todas las propiedades del sistema
			//Y podemos ejecutar unas cosas u otras en funcion de ello
			Map<String, String> variables = System.getenv();
			//variables.forEach((key, value)-> System.out.println("Key: "+ key + " - Value: "+value));
		}
		
		@Test
		@EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches="/usr/lib/jvm/java-8-oracle")
		void testJavaHome() {
			System.out.println("Variable de entorno");
		}
	}
	
	@Nested
	@Tag("version")
	class JavaVersion {
		
		@Test
		@EnabledOnJre(JRE.JAVA_8)
		void soloJdk8() {
			System.out.println("Estoy en Java 8");
		}
		
		@Test
		@DisabledOnJre(JRE.JAVA_15)
		void noTestJdk15() {
			System.out.println("No estoy en Java 15");
		}
	}

	@Nested
	@Tag("timeout")
	@DisplayName("Pruebas de time out")
	class TimeOut {
		
		@Test
		@Timeout(5)
		void testTimeOut() throws InterruptedException {
			TimeUnit.SECONDS.sleep(3);
		}
		
		@Test
		@Timeout(value=200, unit = TimeUnit.MILLISECONDS)
		void testTimeOut2() throws InterruptedException {
			TimeUnit.SECONDS.sleep(5);
		}
		
		@Test
		void testTimeOut3() {
			Assertions.assertTimeout(Duration.ofSeconds(4), 
					() -> TimeUnit.SECONDS.sleep(7));
		}
	}
	
}
