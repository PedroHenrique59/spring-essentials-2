package academy.devdojo.springboot2.service;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.exception.BadRequestException;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.util.AnimePutRequestBodyCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {
    @InjectMocks
    private AnimeService animeService;

    @Mock
    private AnimeRepository animeRepository;

    @BeforeEach
    public void setUp() {
        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepository.findAll(ArgumentMatchers.any(PageRequest.class))).thenReturn(animePage);

        BDDMockito.when(animeRepository.findAll()).thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepository.findByName(ArgumentMatchers.anyString())).thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepository.save(ArgumentMatchers.any(Anime.class))).thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.doNothing().when(animeRepository).delete(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("list returns list of anime inside page object when successful")
    public void list_ReturnsListOfAnimeInsidePageObject_WhenSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();
        Page<Anime> animePage = animeService.listAll(PageRequest.of(1,1));

        Assertions.assertThat(animePage).isNotNull();

        Assertions.assertThat(animePage.toList()).isNotEmpty().hasSize(1);

        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("listAll returns list of anime when successful")
    public void listAll_ReturnsListOfAnimes_WhenSuccessful() {
        List<Anime> animeList = animeService.listAllNonPageable();

        Assertions.assertThat(animeList).isNotNull();
        Assertions.assertThat(animeList).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    @DisplayName("findById returns anime when successful")
    public void findById_ReturnsAnime_WhenSuccessful() {
        Long expectedId = AnimeCreator.createValidAnime().getId();

        Anime anime = animeService.findById(1L);

        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findById throws BadRequestException when anime is not found")
    public void findById_ThrowsBadRequestException_WhenAnimeIsNotFound() {
       BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

       Assertions.assertThatExceptionOfType(BadRequestException.class).isThrownBy(() -> animeService.findById(1L));
    }

    @Test
    @DisplayName("findByName returns list of anime when successful")
    public void findByName_ReturnsAnime_WhenSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();

        List<Anime> animeList = animeService.findByName(expectedName);

        Assertions.assertThat(animeList).isNotNull().isNotEmpty();

        Assertions.assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName returns an empty list of anime when anime is not found")
    public void findByName_ReturnsEmptyListOfAnime_WhenAnimeIsNotFound() {
        BDDMockito.when(animeRepository.findByName(ArgumentMatchers.anyString())).thenReturn(Collections.emptyList());

        List<Anime> animes = animeService.findByName("test");

        Assertions.assertThat(animes).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("save returns anime when successful")
    public void save_ReturnsAnime_WhenSuccessful() {
        Anime anime = animeService.save(AnimePostRequestBodyCreator.createAnimePostRequestBody());

        Assertions.assertThat(anime).isNotNull().isEqualTo(AnimeCreator.createValidAnime());
    }

    @Test
    @DisplayName("replace updates anime when successful")
    public void replace_UpdatesAnime_WhenSuccessful() {
        Assertions.assertThatCode(() -> animeService.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody())).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("delete removes anime when successful")
    public void delete_RemovesAnime_WhenSuccessful() {
        Assertions.assertThatCode(() -> animeService.delete(1L)).doesNotThrowAnyException();
    }
}
