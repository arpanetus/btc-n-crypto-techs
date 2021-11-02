import junit.framework.TestCase;

public class TxHandlerTest extends TestCase {

    private KeyPair initialParent, child1KP, child2KP, grandChildKP;
    private Transaction genesisTxn;
    private TxHandler txHandler;


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // first we have to initialize the keypairs
        KeyPairGenerator keyPairGenerator = new KeyPairGenerator();
        initialParent = keyPairGenerator.generateKeyPair();
        child1KP = keyPairGenerator.generateKeyPair();
        child2KP = keyPairGenerator.generateKeyPair();
        grandChildKP = keyPairGenerator.generateKeyPair();


        // now we can initialize the genesis transaction
        genesisTxn = new Transaction();
        genesisTxn.addOutput(100, initialParent.getPublicKey());
        genesisTxn.finalize();

        UTXOPool pool = new UTXOPool();
        
        // add genesis transaction to the pool
        UTXO utxo = new UTXO(genesisTxn.getHash(), 0);
        pool.addUTXO(utxo, genesisTxn.getOutput(0));
        
        txHandler = new TxHandler(pool);
    }

    public void testValidTxValue() {
        Transaction tx = new Transaction();
        tx.addInput(genesisTxn.getHash(), 0);
        tx.addOutput(50, child1KP.getPublicKey());
        tx.addOutput(50, child2KP.getPublicKey());
        tx.finalize();

        assertTrue(txHandler.isValidTx(tx));
    }
}