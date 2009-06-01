package com.pferrot.sharedcalendar.model.movie;
// Generated 10 oct. 2008 00:01:18 by Hibernate Tools 3.2.0.b9

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.MapKey;
import org.hibernate.envers.Audited;

import com.pferrot.sharedcalendar.i18n.I18nConsts;
import com.pferrot.sharedcalendar.model.Person;

/**
 * @author Patrice
 *
 */
@Entity
@Table(name = "MOVIES")
public class Movie implements Serializable {

	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;
	
	// TODO: Added this field to make it easy to retrieve movies ordered
	// by title. Not sure how to do with the "titles" map below.
	@Column(name = "TITLE", nullable = false, length = 255)
	@Audited
	private String title;
	
	@CollectionOfElements(targetElement = String.class)
	@JoinTable(name = "MOVIES_TITLES",
			joinColumns = @JoinColumn(name = "MOVIE_ID"))
	@MapKey(targetElement = String.class)
	@Audited
	private Map<String, String> titles = new HashMap<String, String>();
	
	@CollectionOfElements(targetElement = String.class)
	@JoinTable(name = "MOVIES_DESCRIPTIONS",
			joinColumns = @JoinColumn(name = "MOVIE_ID"))
	@MapKey(targetElement = String.class)
	@Audited
	private Map<String, String> descriptions = new HashMap<String, String>();
	
	@Column(name = "YEAR", nullable = true)
	@Audited
    private Integer year;
	
	@Column(name = "DURATION", nullable = true)
	@Audited
    private Integer duration;
	
	@ManyToMany(targetEntity = MovieCategory.class)
	private Set<MovieCategory> categories = new HashSet<MovieCategory>();
	
	@ManyToMany(targetEntity = Person.class)
	@JoinTable(name = "MOVIES_ACTORS",
			joinColumns = @JoinColumn(name = "MOVIE_ID"),
			inverseJoinColumns = @JoinColumn(name = "PERSON_ID"))
	private Set<Person> actors = new HashSet<Person>();
	
	@ManyToMany(targetEntity = Person.class)
	@JoinTable(name = "MOVIES_DIRECTORS",
			joinColumns = @JoinColumn(name = "MOVIE_ID"),
			inverseJoinColumns = @JoinColumn(name = "PERSON_ID"))
	private Set<Person> directors = new HashSet<Person>();
	
	@OneToMany(mappedBy = "movie", targetEntity = MovieInstance.class)
	private Set<MovieInstance> movieInstances = new HashSet<MovieInstance>();
	
	@Version
	@Column(name = "OBJ_VERSION")
	private int version;	
	
    public Movie() {
    	super();
    }
   
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }    
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		// See comment on "title" field above.
		setTitle(I18nConsts.DEFAULT_LANGUAGE, title);
	}

	// Private methods, see comment on "title" field above. 
	private Map<String, String> getTitles() {
		return titles;
	}

	private void setTitles(Map<String, String> titles) {
		this.titles = titles;
	}

	private void setTitle(String language, String title) {
		titles.put(language, title);
	}
	
	private String getTitle(String language) {
		return titles.get(language);
	}

	public Map<String, String> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(Map<String, String> descriptions) {
		this.descriptions = descriptions;
	}

	public void setDescription(String language, String title) {
		descriptions.put(language, title);
	}
	
	public String getDescription(String description) {
		return descriptions.get(description);
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Set<MovieCategory> getCategories() {
		return categories;
	}

	public void setCategories(Set<MovieCategory> categories) {
		this.categories = categories;
	}

	public Set<Person> getActors() {
		return actors;
	}

	public void setActors(Set<Person> actors) {
		this.actors = actors;
	}

	public Set<Person> getDirectors() {
		return directors;
	}

	public void setDirectors(Set<Person> directors) {
		this.directors = directors;
	}

	public Set<MovieInstance> getMovieInstances() {
		return movieInstances;
	}

	public void setMovieInstances(Set<MovieInstance> movieInstances) {
		this.movieInstances = movieInstances;
	}
}

