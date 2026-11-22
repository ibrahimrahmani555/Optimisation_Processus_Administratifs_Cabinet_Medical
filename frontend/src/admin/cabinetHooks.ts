import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { createCabinet, deleteCabinetImage, getCabinet, getCabinets, updateCabinet, updateCabinetStatus, uploadCabinetImage } from './cabinetApi';
import type { CabinetListParams } from './cabinetApi';

export const cabinetListQueryKey = ['admin', 'cabinets'];

export function useCabinets(params: CabinetListParams) {
  return useQuery({
    queryKey: [...cabinetListQueryKey, params],
    queryFn: () => getCabinets(params),
  });
}

export function useCabinetDetail(id: string | null) {
  return useQuery({
    queryKey: [...cabinetListQueryKey, 'detail', id],
    queryFn: () => getCabinet(id as string),
    enabled: Boolean(id),
  });
}

function invalidateCabinetQueries(queryClient: ReturnType<typeof useQueryClient>) {
  queryClient.invalidateQueries({ queryKey: cabinetListQueryKey });
  queryClient.invalidateQueries({ queryKey: ['admin', 'dashboard'] });
}

export function useCabinetMutations() {
  const queryClient = useQueryClient();
  const onSuccess = () => invalidateCabinetQueries(queryClient);

  return {
    create: useMutation({ mutationFn: createCabinet, onSuccess }),
    update: useMutation({ mutationFn: ({ id, name }: { id: string; name: string }) => updateCabinet(id, name), onSuccess }),
    status: useMutation({ mutationFn: ({ id, active }: { id: string; active: boolean }) => updateCabinetStatus(id, active), onSuccess }),
    uploadImage: useMutation({ mutationFn: ({ id, file }: { id: string; file: File }) => uploadCabinetImage(id, file), onSuccess }),
    deleteImage: useMutation({ mutationFn: (id: string) => deleteCabinetImage(id), onSuccess }),
  };
}
