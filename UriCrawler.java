package crawler;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Document;
import document.RetrievedDocument;

/**
 * A simplified web crawler, specialized to crawl local URIs rather
 * than to retrieve remote documents.
 * 
 * @author liberato
 *
 */
public class UriCrawler {
int maxVisit = 0;
Set<URI> toBeVisited = new HashSet<URI>();
Set<URI> getVisited = new HashSet<URI>();
Set<RetrievedDocument> getRetrieve = new HashSet<RetrievedDocument>();

	/**
	 * Instantiates a new UriCrawler. The maximum number of documents a crawler
	 * will attempt to visit, ever, is limited to visitQuota.
	 * 
	 * @param visitQuota
	 *            the maximum number of documents a crawler will attempt to
	 *            visit
	 * @throws IllegalArgumentException
	 *             if maximumRetrievalAttempts is less than one
	 */
	public UriCrawler(int visitQuota) throws IllegalArgumentException {
		
		if (visitQuota < 1) {
			throw new IllegalArgumentException();
		}
		
		else{
			maxVisit = visitQuota;
		}
		
	}

	/**
	 * Returns the set of URIs that this crawler has attempted to visit
	 * (successfully or not).
	 * 
	 * @return the set of URIs that this crawler has attempted to visit
	 */
	public Set<URI> getVistedUris() {
		
		return getVisited;
	}
	
	/**
	 * Returns the set of RetrievedDocuments corresponding to the URIs
	 * this crawler has successfully visited.
	 * 
	 * @return the set of RetrievedDocuments corresponding to the URIs
	 * this crawler has successfully visited
	 */
	public Set<RetrievedDocument> getVisitedDocuments() {
	
		return getRetrieve;
		
	}

	/**
	 * Adds a URI to the collections of URIs that this crawler should attempt to
	 * visit.
	 * 
	 * @param uri
	 *            the URI to be visited
	 */
	public void addUri(URI uri) {
		if(!getVisited.contains(uri))
		toBeVisited.add(uri);
	}

	/**
	 * Attempts to visit a single as-yet unattempted URI in this crawler's
	 * collection of to-be-visited URIs.
	 * 
	 * Visiting a document entails parsing the text and links from the URI.
	 * 
	 * If the parse succeeds:
	 * 
	 * - The "file:" links should be added to this crawler's collection of
	 * to-be-visited URIs.
	 * 
	 * - A new RetrievedDocument should be added to this crawler's collection of
	 * successfully visited documents.
	 * 
	 * If the parse fails, this method considers the visit attempted but
	 * unsuccessful.
	 * 
	 * @throws MaximumVisitsExceededException
	 *             if this crawler has already attempted to visit its quota of
	 *             visits
	 * @throws NoUnvisitedUrisException
	 *             if no more unattempted URI remain in this crawler's
	 *             collection of URIs to visit
	 */
	public void visitOne() throws MaximumVisitsExceededException, NoUnvisitedUrisException {
		
		Iterator<URI> sam = toBeVisited.iterator();
		if(!sam.hasNext()) throw new NoUnvisitedUrisException();
		if(getVisited.size()==maxVisit) throw new MaximumVisitsExceededException();
		
		URI currentlyVisiting = sam.next();
		toBeVisited.remove(currentlyVisiting);
		Document kek = CrawlerUtils.parse(currentlyVisiting);
		if(kek != null){
			RetrievedDocument docrec = new RetrievedDocument(currentlyVisiting,kek.text(),CrawlerUtils.getFileUriLinks(kek));
			getRetrieve.add(docrec);
			for(URI re : docrec.links){
				if(!getVisited.contains(re)){
					toBeVisited.add(re);
				}
			}
		}
		getVisited.add(currentlyVisiting);
		
		
	}

	/**
	 * Attempts to visit all URIs in this crawler (and any URIs they reference,
	 * and so on).
	 * 
	 * This method will not raise a MaximumVisitsExceededException if there are
	 * more URIs than can be visited. It will instead stop once the UriCrawler's
	 * quota has been reached.
	 */
	public void visitAll() {
		while(getVisited.size()<=maxVisit && toBeVisited.size()>0){
		try {
			visitOne();
		} catch (MaximumVisitsExceededException | NoUnvisitedUrisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}
		
		
		
	}
}
