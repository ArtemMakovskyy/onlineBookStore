package online.book.store.repository.book;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.book.store.model.Book;
import online.book.store.repository.SpecificationProvider;
import online.book.store.repository.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationProviderManagerImpl implements SpecificationProviderManager<Book> {
    private final List<SpecificationProvider<Book>> phoneSpecificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return phoneSpecificationProviders.stream()
                .filter(b -> b.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Can't find correct specification provider for key " + key));
    }
}
