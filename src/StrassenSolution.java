import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class solves the Towers problem using Strassen's Algorithm from
 * HackerRank problem set. It is a part of B503 project.
 * 
 * @author jaynagle
 *
 */
public class StrassenSolution {

	/**
	 * MOD is a constant used to limit the answer to a scaled value by a factor
	 * of 10^7+7.
	 */
	private static final BigInteger MOD = BigInteger.valueOf(1000000007);

	/**
	 * brickIndicatorVector stores which bricks are available as boolean flags.
	 */
	private BigInteger[] brickIndicatorVector = new BigInteger[17];

	/**
	 * vector stores values number of towers up to height 16 for given set of
	 * bricks which is calculated using dynamic programming.
	 */
	private BigInteger[] vector = new BigInteger[17];

	/**
	 * Main matrix used for calculation of high value inputs.
	 */
	private BigInteger[][] matrix = new BigInteger[16][16];

	/**
	 * Main method which launches the program.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		StrassenSolution solution = new StrassenSolution();
		solution.matrixCalculation(solution.readInput());
	}

	/**
	 * This method reads input attributes from STDIN.
	 * 
	 * @return
	 */
	private List<String> readInput() {
		List<String> inputList = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input;

			while ((input = br.readLine()) != null) {
				inputList.add(input);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputList;
	}

	/**
	 * This method performs necessary matrix calculations to find the final
	 * answer (Number of possible towers).
	 * 
	 * @param inputList
	 */
	private void matrixCalculation(List<String> inputList) {
		BigInteger towerHeight = new BigInteger(inputList.get(0));
		int numOfDistinctBricks = Integer.parseInt(inputList.get(1));
		String[] brickHeightsStringArray = inputList.get(2).split(" ");

		int[] brickHeightsIntArray = new int[brickHeightsStringArray.length];
		for (int i = 0; i < brickHeightsStringArray.length; i++) {
			brickHeightsIntArray[i] = Integer.parseInt(brickHeightsStringArray[i]);
		}

		/**
		 * This block initializes the main matrix used to calculate final
		 * answer.
		 */
		for (int i = 0; i < 16; ++i) {
			for (int j = 0; j < 16; ++j) {
				if (i + 1 == j) {
					matrix[i][j] = BigInteger.valueOf(1);
				} else {
					matrix[i][j] = BigInteger.valueOf(0);
				}
			}
		}

		/**
		 * This block initializes multiplication vector and height flag array.
		 */
		for (int i = 0; i < 17; ++i) {
			for (int j = 0; j < 17; ++j) {
				vector[i] = BigInteger.valueOf(0);
				brickIndicatorVector[i] = BigInteger.valueOf(0);
			}
		}

		for (int i = 0; i < numOfDistinctBricks; i++) {
			matrix[15][15 - brickHeightsIntArray[i] + 1] = BigInteger.valueOf(1);
			brickIndicatorVector[brickHeightsIntArray[i] - 1] = BigInteger.valueOf(1);
		}

		/**
		 * createVector method calculates and populates values of number of
		 * towers up to height 16.
		 */
		createVector(brickIndicatorVector, BigInteger.valueOf(17));

		/**
		 * This line rotates the array to eliminate f(0) value for number of
		 * towers.
		 */
		vector = Arrays.copyOfRange(vector, 1, 17);

		BigInteger result = BigInteger.valueOf(0);
		if (towerHeight.compareTo(BigInteger.valueOf(16L)) <= 0) {
			/**
			 * This block pulls out values for number of towers up to height 16.
			 */
			result = (vector[towerHeight.subtract(BigInteger.valueOf(1)).intValue()]).multiply(BigInteger.valueOf(2))
					.mod(MOD);
		} else {
			/**
			 * This block computes values for number of towers greater than 16.
			 */
			BigInteger[][] R = getExponent(matrix, towerHeight.subtract(BigInteger.valueOf(16L)));
			for (int j = 0; j < 16; j++) {
				result = (result.add(R[15][j].multiply(vector[j]))).mod(MOD);
			}
			result = result.multiply(BigInteger.valueOf(2)).mod(MOD);
		}

		submitOutput(result);
	}

	/**
	 * This method publishes the computed output on STDOUT.
	 * 
	 * @param result
	 */
	private void submitOutput(BigInteger result) {
		try {
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out));
			output.write(String.valueOf(result));
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This block calculates nth power of matrix using Strassen's algorithm.
	 * 
	 * @param matrix
	 * @param power
	 * @return
	 */
	public BigInteger[][] getExponent(BigInteger[][] matrix, BigInteger power) {
		if (power.compareTo(BigInteger.valueOf(0L)) == 0) {
			BigInteger[][] identityMatrix = new BigInteger[16][16];
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					if (i == j)
						identityMatrix[i][j] = BigInteger.valueOf(1);
					else
						identityMatrix[i][j] = BigInteger.valueOf(0);
				}
			}
			return identityMatrix;
		} else if (power.mod(BigInteger.valueOf(2L)) == BigInteger.valueOf(0L)) {
			BigInteger[][] R = getExponent(matrix, power.divide(BigInteger.valueOf(2L)));
			return strassenMultiply(R, R);
		} else {
			BigInteger[][] R = getExponent(matrix, power.subtract(BigInteger.valueOf(1L)));
			return strassenMultiply(R, matrix);
		}
	}

	/**
	 * This method computes values of number of towers for height up to 16.
	 * 
	 * @param brickIndicatorVector
	 * @param towerHeight
	 * @return
	 */
	private BigInteger createVector(BigInteger[] brickIndicatorVector, BigInteger towerHeight) {
		if (towerHeight.compareTo(BigInteger.valueOf(0)) == 0) {
			return BigInteger.valueOf(1);
		}
		BigInteger k = createVector(brickIndicatorVector, towerHeight.subtract(BigInteger.valueOf(1)));
		vector[towerHeight.subtract(BigInteger.valueOf(1)).intValue()] = k;

		int j = towerHeight.intValue();
		BigInteger tmpHeight = BigInteger.valueOf(0);
		while (j > 0) {
			tmpHeight = tmpHeight.add(brickIndicatorVector[j - 1]
					.multiply(vector[towerHeight.subtract(BigInteger.valueOf(j)).intValue()]));
			j--;
		}
		return tmpHeight;
	}

	/**
	 * This function takes 2 matrices as arguments and calculates their product
	 * using Strassen's method.
	 * 
	 * @param matrix1
	 * @param matrix2
	 * @return
	 */
	public BigInteger[][] strassenMultiply(BigInteger[][] matrix1, BigInteger[][] matrix2) {
		int order = matrix1.length;
		BigInteger[][] resultantMatrix = new BigInteger[order][order];

		if (order == 1)
			resultantMatrix[0][0] = matrix1[0][0].multiply(matrix2[0][0]);
		else {
			/**
			 * Initialize 4 sub-matrices corresponding to every input matrix.
			 */
			BigInteger[][] m11 = new BigInteger[order / 2][order / 2];
			BigInteger[][] m12 = new BigInteger[order / 2][order / 2];
			BigInteger[][] m21 = new BigInteger[order / 2][order / 2];
			BigInteger[][] m22 = new BigInteger[order / 2][order / 2];
			BigInteger[][] n11 = new BigInteger[order / 2][order / 2];
			BigInteger[][] n12 = new BigInteger[order / 2][order / 2];
			BigInteger[][] n21 = new BigInteger[order / 2][order / 2];
			BigInteger[][] n22 = new BigInteger[order / 2][order / 2];

			/**
			 * Split each matrix into 4 parts.
			 */
			splitMatrices(matrix1, m11, 0, 0);
			splitMatrices(matrix1, m12, 0, order / 2);
			splitMatrices(matrix1, m21, order / 2, 0);
			splitMatrices(matrix1, m22, order / 2, order / 2);
			splitMatrices(matrix2, n11, 0, 0);
			splitMatrices(matrix2, n12, 0, order / 2);
			splitMatrices(matrix2, n21, order / 2, 0);
			splitMatrices(matrix2, n22, order / 2, order / 2);

			/**
			 * Calculate M1 - M7 multiplications required for finding final
			 * matrix.
			 */
			BigInteger[][] m1 = strassenMultiply(addMatrices(m11, m22), addMatrices(n11, n22));
			BigInteger[][] m2 = strassenMultiply(addMatrices(m21, m22), n11);
			BigInteger[][] m3 = strassenMultiply(m11, subtractMatrices(n12, n22));
			BigInteger[][] m4 = strassenMultiply(m22, subtractMatrices(n21, n11));
			BigInteger[][] m5 = strassenMultiply(addMatrices(m11, m12), n22);
			BigInteger[][] m6 = strassenMultiply(subtractMatrices(m21, m11), addMatrices(n11, n12));
			BigInteger[][] m7 = strassenMultiply(subtractMatrices(m12, m22), addMatrices(n21, n22));

			/**
			 * Calculate components of resultant matrix.
			 */
			BigInteger[][] c11 = addMatrices(subtractMatrices(addMatrices(m1, m4), m5), m7);
			BigInteger[][] c12 = addMatrices(m3, m5);
			BigInteger[][] c21 = addMatrices(m2, m4);
			BigInteger[][] c22 = addMatrices(subtractMatrices(addMatrices(m1, m3), m2), m6);

			/**
			 * Merge split matrices to form the resultant matrix.
			 */
			mergeMatrices(c11, resultantMatrix, 0, 0);
			mergeMatrices(c12, resultantMatrix, 0, order / 2);
			mergeMatrices(c21, resultantMatrix, order / 2, 0);
			mergeMatrices(c22, resultantMatrix, order / 2, order / 2);
		}
		return resultantMatrix;
	}

	/**
	 * Subtract 2 input matrices.
	 * 
	 * @param m1
	 * @param m2
	 * @return
	 */
	public BigInteger[][] subtractMatrices(BigInteger[][] m1, BigInteger[][] m2) {
		int n = m1.length;
		BigInteger[][] subtractedMatrices = new BigInteger[n][n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				subtractedMatrices[i][j] = m1[i][j].subtract(m2[i][j]);
		return subtractedMatrices;
	}

	/**
	 * Add 2 input matrices.
	 * 
	 * @param m1
	 * @param m2
	 * @return
	 */
	public BigInteger[][] addMatrices(BigInteger[][] m1, BigInteger[][] m2) {
		int n = m1.length;
		BigInteger[][] addedMatrices = new BigInteger[n][n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				addedMatrices[i][j] = m1[i][j].add(m2[i][j]);
		return addedMatrices;
	}

	/**
	 * Split input matrices into 4.
	 * 
	 * @param matrix
	 * @param splitInstance
	 * @param startI
	 * @param startJ
	 */
	public void splitMatrices(BigInteger[][] matrix, BigInteger[][] splitInstance, int startI, int startJ) {
		for (int i = 0; i < splitInstance.length; i++)
			for (int j = 0; j < splitInstance.length; j++)
				splitInstance[i][j] = matrix[i + startI][j + startJ];
	}

	/**
	 * Merge component matrices to form resultant.
	 * 
	 * @param splitInstance
	 * @param mergedMatrix
	 * @param startI
	 * @param startJ
	 */
	public void mergeMatrices(BigInteger[][] splitInstance, BigInteger[][] mergedMatrix, int startI, int startJ) {
		for (int i = 0; i < splitInstance.length; i++)
			for (int j = 0; j < splitInstance.length; j++)
				mergedMatrix[i + startI][j + startJ] = splitInstance[i][j].mod(MOD);
	}
}
