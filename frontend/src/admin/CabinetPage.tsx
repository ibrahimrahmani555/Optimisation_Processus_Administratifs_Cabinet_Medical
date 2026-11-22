import { useState } from 'react';
import { Building2, Eye, Pencil, Plus, Power, RotateCcw, Search, X } from 'lucide-react';
import { useCabinetDetail, useCabinetMutations, useCabinets } from './cabinetHooks';
import { getCabinetImageUrl } from './cabinetApi';
import type { Cabinet, CabinetStatusFilter } from './cabinetTypes';

interface CabinetFormModalProps {
  cabinet?: Cabinet | null;
  onClose: () => void;
  onSubmit: (name: string, imageFile: File | null) => void;
  pending: boolean;
  error: string | null;
}

function CabinetFormModal({ cabinet, onClose, onSubmit, pending, error }: CabinetFormModalProps) {
  const [name, setName] = useState(cabinet?.name || '');
  const [imageFile, setImageFile] = useState<File | null>(null);
  const [imagePreview, setImagePreview] = useState<string | null>(getCabinetImageUrl(cabinet?.imageUrl || null));
  const [validationError, setValidationError] = useState('');

  const submit = (event: React.FormEvent) => {
    event.preventDefault();
    const trimmed = name.trim();
    if (!trimmed) {
      setValidationError('Le nom du cabinet est obligatoire.');
      return;
    }
    if (trimmed.length > 150) {
      setValidationError('Le nom ne doit pas dépasser 150 caractères.');
      return;
    }
    setValidationError('');
    onSubmit(trimmed, imageFile);
  };

  return (
    <div className="modal-backdrop" role="presentation" onMouseDown={(event) => event.target === event.currentTarget && onClose()}>
      <section className="cabinet-modal" role="dialog" aria-modal="true" aria-labelledby="cabinet-form-title">
        <div className="modal-heading">
          <div><p>Cabinet</p><h2 id="cabinet-form-title">{cabinet ? 'Modifier le cabinet' : 'Ajouter un cabinet'}</h2></div>
          <button type="button" className="modal-close" onClick={onClose} aria-label="Fermer"><X size={17} /></button>
        </div>
        <form onSubmit={submit}>
          <label className="cabinet-field">
            <span>Nom du cabinet</span>
            <input value={name} onChange={(event) => setName(event.target.value)} maxLength={150} placeholder="Ex. Cabinet Médical Central" autoFocus />
          </label>
          <label className="cabinet-field cabinet-image-field">
            <span>Image du cabinet</span>
            {imagePreview && <img className="cabinet-image-preview" src={imagePreview} alt="Aperçu du cabinet" />}
            <input
              type="file"
              accept="image/jpeg,image/png,image/webp"
              onChange={(event) => {
                const file = event.target.files?.[0] || null;
                setImageFile(file);
                setImagePreview(file ? URL.createObjectURL(file) : getCabinetImageUrl(cabinet?.imageUrl || null));
              }}
            />
          </label>
          {(validationError || error) && <div className="alert-error" role="alert"><span>!</span>{validationError || error}</div>}
          <div className="modal-actions">
            <button type="button" className="secondary" onClick={onClose}>Annuler</button>
            <button type="submit" className="primary" disabled={pending}>{pending ? 'Enregistrement...' : 'Enregistrer'}</button>
          </div>
        </form>
      </section>
    </div>
  );
}

function CabinetDetailsModal({ id, onClose }: { id: string; onClose: () => void }) {
  const detail = useCabinetDetail(id);
  const cabinet = detail.data;

  return (
    <div className="modal-backdrop" role="presentation" onMouseDown={(event) => event.target === event.currentTarget && onClose()}>
      <section className="cabinet-modal" role="dialog" aria-modal="true" aria-labelledby="cabinet-detail-title">
        <div className="modal-heading">
          <div><p>Détail du cabinet</p><h2 id="cabinet-detail-title">{cabinet?.name || 'Chargement...'}</h2></div>
          <button type="button" className="modal-close" onClick={onClose} aria-label="Fermer"><X size={17} /></button>
        </div>
        {detail.isPending && <div className="cabinet-empty">Chargement...</div>}
        {detail.isError && <div className="alert-error" role="alert"><span>!</span>Impossible de charger ce cabinet.</div>}
        {cabinet && (
          <>
            {getCabinetImageUrl(cabinet.imageUrl) && (
              <img className="cabinet-detail-image" src={getCabinetImageUrl(cabinet.imageUrl)!} alt={cabinet.name} />
            )}
            <dl className="cabinet-details">
              <div><dt>Identifiant</dt><dd>{cabinet.id}</dd></div>
              <div><dt>Date de création</dt><dd>{formatDate(cabinet.createdAt)}</dd></div>
              <div><dt>Statut</dt><dd><span className={`status-badge ${cabinet.active ? 'success' : 'danger'}`}>{cabinet.active ? 'Actif' : 'Inactif'}</span></dd></div>
              <div><dt>Médecins</dt><dd>{cabinet.doctors}</dd></div>
              <div><dt>Secrétaires</dt><dd>{cabinet.secretaries}</dd></div>
              <div><dt>Patients</dt><dd>{cabinet.patients}</dd></div>
            </dl>
            <div className="modal-actions"><button type="button" className="secondary" onClick={onClose}>Fermer</button></div>
          </>
        )}
      </section>
    </div>
  );
}

function CabinetStatusModal({ cabinet, onClose, onConfirm, pending, error }: {
  cabinet: Cabinet;
  onClose: () => void;
  onConfirm: () => void;
  pending: boolean;
  error?: string | null;
}) {
  return (
    <div className="modal-backdrop" role="presentation" onMouseDown={(event) => event.target === event.currentTarget && onClose()}>
      <section className="cabinet-modal cabinet-confirm" role="dialog" aria-modal="true" aria-labelledby="cabinet-status-title">
        <div className="modal-heading">
          <div><p>Confirmation</p><h2 id="cabinet-status-title">{cabinet.active ? 'Désactiver le cabinet' : 'Activer le cabinet'}</h2></div>
        </div>
        <p>{cabinet.active
          ? `Le cabinet « ${cabinet.name} » sera désactivé. Ses utilisateurs ne pourront plus accéder aux ressources protégées.`
          : `Le cabinet « ${cabinet.name} » sera activé.`}</p>
        {error && <div className="alert-error" role="alert"><span>!</span>{error}</div>}
        <div className="modal-actions">
          <button type="button" className="secondary" onClick={onClose}>Annuler</button>
          <button type="button" className={cabinet.active ? 'danger-button' : 'primary'} onClick={onConfirm} disabled={pending}>
            {pending ? 'Modification...' : cabinet.active ? 'Désactiver' : 'Activer'}
          </button>
        </div>
      </section>
    </div>
  );
}

function formatDate(value: string) {
  return new Intl.DateTimeFormat('fr-FR', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value));
}

export default function CabinetPage() {
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [search, setSearch] = useState('');
  const [submittedSearch, setSubmittedSearch] = useState('');
  const [status, setStatus] = useState<CabinetStatusFilter>('ALL');
  const [formCabinet, setFormCabinet] = useState<Cabinet | null | undefined>(undefined);
  const [detailsId, setDetailsId] = useState<string | null>(null);
  const [statusCabinet, setStatusCabinet] = useState<Cabinet | null>(null);
  const [mutationError, setMutationError] = useState<string | null>(null);

  const cabinets = useCabinets({ page, size, search: submittedSearch, status, sort: 'createdAt,desc' });
  const mutations = useCabinetMutations();

  const submitSearch = (event: React.FormEvent) => {
    event.preventDefault();
    setSubmittedSearch(search.trim());
    setPage(0);
  };

  const resetSearch = () => {
    setSearch('');
    setSubmittedSearch('');
    setPage(0);
  };

  const closeForm = () => {
    setFormCabinet(undefined);
    setMutationError(null);
  };

  const submitCabinet = (name: string, imageFile: File | null) => {
    setMutationError(null);
    if (formCabinet) {
      mutations.update.mutate({ id: formCabinet.id, name }, {
        onSuccess: (updatedCabinet) => {
          if (imageFile) {
            mutations.uploadImage.mutate({ id: updatedCabinet.id, file: imageFile }, {
              onSuccess: closeForm,
              onError: (error) => setMutationError(error instanceof Error ? error.message : "Impossible d'enregistrer l'image."),
            });
          } else {
            closeForm();
          }
        },
        onError: (error) => setMutationError(error instanceof Error ? error.message : 'Impossible de modifier le cabinet.'),
      });
    } else {
      mutations.create.mutate(name, {
        onSuccess: (createdCabinet) => {
          if (imageFile) {
            mutations.uploadImage.mutate({ id: createdCabinet.id, file: imageFile }, {
              onSuccess: closeForm,
              onError: (error) => setMutationError(error instanceof Error ? error.message : "Le cabinet est créé, mais l'image n'a pas pu être enregistrée."),
            });
          } else {
            closeForm();
          }
        },
        onError: (error) => setMutationError(error instanceof Error ? error.message : 'Impossible de créer le cabinet.'),
      });
    }
  };

  const confirmStatus = () => {
    if (!statusCabinet) return;
    setMutationError(null);
    mutations.status.mutate({ id: statusCabinet.id, active: !statusCabinet.active }, {
      onSuccess: () => setStatusCabinet(null),
      onError: (error) => setMutationError(error instanceof Error ? error.message : 'Impossible de modifier le statut.'),
    });
  };

  return (
    <main>
      <div className="page-heading">
        <div><p>Administration</p><h1>Gestion des cabinets</h1></div>
        <button type="button" className="primary cabinet-add" onClick={() => setFormCabinet(null)}><Plus size={15} />Ajouter un cabinet</button>
      </div>

      {cabinets.data && (
        <section className="cabinet-stat-grid" aria-label="Statistiques des cabinets">
          <article className="card cabinet-stat-card"><span className="cabinet-stat-icon"><Building2 size={17} /></span><div><p>Total des cabinets</p><strong>{cabinets.data.summary.total}</strong></div></article>
          <article className="card cabinet-stat-card"><span className="cabinet-stat-icon success"><Building2 size={17} /></span><div><p>Cabinets actifs</p><strong>{cabinets.data.summary.active}</strong></div></article>
          <article className="card cabinet-stat-card"><span className="cabinet-stat-icon danger"><Building2 size={17} /></span><div><p>Cabinets inactifs</p><strong>{cabinets.data.summary.inactive}</strong></div></article>
        </section>
      )}

      <section className="card cabinet-panel">
        <div className="cabinet-toolbar">
          <form className="cabinet-search" onSubmit={submitSearch}>
            <Search size={15} />
            <input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Rechercher un cabinet..." aria-label="Rechercher un cabinet" />
            {search && <button type="button" onClick={resetSearch} aria-label="Réinitialiser la recherche"><X size={14} /></button>}
          </form>
          <div className="cabinet-filters" aria-label="Filtrer par statut">
            {([
              ['ALL', 'Tous'],
              ['ACTIVE', 'Actifs'],
              ['INACTIVE', 'Inactifs'],
            ] as [CabinetStatusFilter, string][]).map(([value, label]) => (
              <button type="button" key={value} className={status === value ? 'active' : ''} onClick={() => { setStatus(value); setPage(0); }}>{label}</button>
            ))}
          </div>
          <button type="button" className="cabinet-refresh" onClick={() => cabinets.refetch()} disabled={cabinets.isFetching} title="Actualiser"><RotateCcw size={15} /></button>
        </div>

        {cabinets.isPending && <div className="cabinet-loading" role="status">Chargement des cabinets...</div>}
        {cabinets.isError && <div className="alert-error" role="alert"><span>!</span>Impossible de charger les cabinets. <button type="button" className="inline-action" onClick={() => cabinets.refetch()}>Réessayer</button></div>}
        {cabinets.data && (
          <>
            <div className="cabinet-table-wrap">
              <table className="cabinet-table">
                <thead><tr><th>Cabinet</th><th>Date de création</th><th>Médecins</th><th>Secrétaires</th><th>Patients</th><th>Statut</th><th>Actions</th></tr></thead>
                <tbody>
                  {cabinets.data.content.map((cabinet) => (
                    <tr key={cabinet.id}>
                      <td>
                        <div className="cabinet-name-cell">
                          {getCabinetImageUrl(cabinet.imageUrl)
                            ? <img className="cabinet-thumbnail" src={getCabinetImageUrl(cabinet.imageUrl)!} alt="" />
                            : <span className="cabinet-thumbnail cabinet-thumbnail-empty"><Building2 size={15} /></span>}
                          <strong>{cabinet.name}</strong>
                        </div>
                      </td>
                      <td>{formatDate(cabinet.createdAt)}</td>
                      <td>{cabinet.doctors}</td>
                      <td>{cabinet.secretaries}</td>
                      <td>{cabinet.patients}</td>
                      <td><span className={`status-badge ${cabinet.active ? 'success' : 'danger'}`}>{cabinet.active ? 'Actif' : 'Inactif'}</span></td>
                      <td>
                        <div className="cabinet-actions">
                          <button type="button" onClick={() => setDetailsId(cabinet.id)} title="Voir les détails" aria-label={`Voir ${cabinet.name}`}><Eye size={14} /></button>
                          <button type="button" onClick={() => setFormCabinet(cabinet)} title="Modifier" aria-label={`Modifier ${cabinet.name}`}><Pencil size={14} /></button>
                          <button type="button" className={cabinet.active ? 'danger' : 'success'} onClick={() => setStatusCabinet(cabinet)} title={cabinet.active ? 'Désactiver' : 'Activer'} aria-label={`${cabinet.active ? 'Désactiver' : 'Activer'} ${cabinet.name}`}><Power size={14} /></button>
                        </div>
                      </td>
                    </tr>
                  ))}
                  {cabinets.data.content.length === 0 && <tr><td colSpan={7}><div className="cabinet-empty">Aucun cabinet trouvé.</div></td></tr>}
                </tbody>
              </table>
            </div>
            {cabinets.data.totalPages > 1 && (
              <div className="cabinet-pagination">
                <span>{cabinets.data.totalElements} résultat{cabinets.data.totalElements > 1 ? 's' : ''}</span>
                <div>
                  <button type="button" className="secondary" disabled={page === 0} onClick={() => setPage((current) => current - 1)}>Précédent</button>
                  <span>Page {page + 1} / {cabinets.data.totalPages}</span>
                  <button type="button" className="secondary" disabled={page + 1 >= cabinets.data.totalPages} onClick={() => setPage((current) => current + 1)}>Suivant</button>
                </div>
              </div>
            )}
          </>
        )}
      </section>

      {formCabinet !== undefined && (
        <CabinetFormModal
          cabinet={formCabinet}
          onClose={closeForm}
          onSubmit={submitCabinet}
          pending={mutations.create.isPending || mutations.update.isPending || mutations.uploadImage.isPending}
          error={mutationError}
        />
      )}
      {detailsId && <CabinetDetailsModal id={detailsId} onClose={() => setDetailsId(null)} />}
      {statusCabinet && <CabinetStatusModal cabinet={statusCabinet} onClose={() => { setStatusCabinet(null); setMutationError(null); }} onConfirm={confirmStatus} pending={mutations.status.isPending} error={mutationError} />}
    </main>
  );
}
