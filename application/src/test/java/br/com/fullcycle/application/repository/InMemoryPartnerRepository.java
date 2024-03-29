package br.com.fullcycle.application.repository;

import br.com.fullcycle.domain.partner.Partner;
import br.com.fullcycle.domain.partner.PartnerId;
import br.com.fullcycle.domain.partner.PartnerRepository;
import br.com.fullcycle.domain.person.Cnpj;
import br.com.fullcycle.domain.person.Email;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class InMemoryPartnerRepository implements PartnerRepository {

    private final Map<String, Partner> partners;
    private final Map<String, Partner> partnersByCnpj;
    private final Map<String, Partner> partnersByEmail;

    public InMemoryPartnerRepository() {
        this.partners = new HashMap<>();
        this.partnersByCnpj = new HashMap<>();
        this.partnersByEmail = new HashMap<>();
    }

    @Override
    public Partner create(final Partner partner) {
        this.partners.put(partner.partnerId().value().toString(), partner);
        this.partnersByCnpj.put(partner.cnpj().value(), partner);
        this.partnersByEmail.put(partner.email().value(), partner);
        return partner;
    }

    @Override
    public Partner update(Partner partner) {
        return null;
    }

    @Override
    public void deleteAll() {
        this.partners.clear();
        this.partnersByCnpj.clear();
        this.partnersByEmail.clear();
    }

    @Override
    public Optional<Partner> partnerOfCnpj(final Cnpj cnpj) {
        return Optional.ofNullable(this.partnersByCnpj.get(Objects.requireNonNull(cnpj.value())));
    }

    @Override
    public Optional<Partner> partnerOfEmail(final Email email) {
        return Optional.ofNullable(this.partnersByEmail.get(Objects.requireNonNull(email.value())));
    }

    @Override
    public Optional<Partner> partnerOfId(final PartnerId id) {
        return Optional.ofNullable(this.partners.get(Objects.requireNonNull(id).value().toString()));
    }
}